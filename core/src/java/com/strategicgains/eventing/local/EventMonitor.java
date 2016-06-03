/*
    Copyright 2011, Strategic Gains, Inc.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package com.strategicgains.eventing.local;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.strategicgains.eventing.EventConsumer;
import com.strategicgains.eventing.EventHandler;

/**
 * A thread that receives published events and sends them to subscribers.
 * Registered event {@link EventConsumer}s will be called for whatever event types each can process.
 * 
 * {@link EventConsumer}s are called using an Executor pool that grows dynamically as needed, so
 * they are run asynchronously.
 * 
 * Events that have no consumers are simply removed from the queue and ignored.
 * 
 * @author toddf
 * @since May 17, 2011
 */
public class EventMonitor
extends Thread
{
	// SECTION: CONSTANTS

	private static final Executor EVENT_EXECUTOR = Executors.newCachedThreadPool();

	
	// SECTION: INSTANCE METHODS

	private Set<EventHandler> handlers = new LinkedHashSet<>();
	private boolean shouldShutDown = false;
	private boolean shouldReRaiseOnError = true;
	private LocalEventChannel eventQueue;
	private long delay;


	// SECTION: CONSTRUCTORS

	public EventMonitor(LocalEventChannel queue, long pollDelayMillis)
	{
		super();
		setDaemon(true);
		this.delay = pollDelayMillis;
		this.eventQueue = queue;
	}

	
	// SECTION: INSTANCE METHODS

	public synchronized boolean register(EventHandler handler)
	{
		return handlers.add(handler);
	}

	public synchronized boolean unregister(EventHandler handler)
	{
		return handlers.remove(handler);
	}

	public void shutdown()
	{
		shouldShutDown = true;
		System.out.println("Event monitor notified for shutdown.");

		synchronized(eventQueue)
		{
			eventQueue.notifyAll();
		}
	}

	public void setReRaiseOnError(boolean value)
	{
		this.shouldReRaiseOnError = value;
	}

	
	// SECTION: RUNNABLE/THREAD

	@Override
	public void run()
	{
		System.out.println("Event monitor starting...");

		while(!shouldShutDown)
		{
			try
			{
				synchronized (eventQueue)
				{
					if (eventQueue.isEmpty())
					{
						eventQueue.wait(delay);		// Support wake-up via eventQueue.notify()
					}
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				System.err.println("Interrupted (use shutdown() to terminate).  Continuing...");
				continue;
			}

			Object event = null;

			while ((event = eventQueue.poll()) != null)
			{
				processEvent(event);
			}
		}
		
		System.out.println("Event monitor exiting...");
		handlers.clear();
	}

	/**
	 * Runs each appropriate EventHandler in an Executor.
	 * 
	 * @param event
	 */
	private void processEvent(final Object event)
    {
	    System.out.println("Processing event: " + event.toString());
	    for (final EventHandler handler : handlers)
	    {
    		EVENT_EXECUTOR.execute(new Runnable(){
				@Override
                public void run()
                {
			    	try
			    	{
			    		handler.handle(event);
			    	}
			    	catch(Exception e)
			    	{
			    		e.printStackTrace();
			    		
			    		if (shouldReRaiseOnError)
			    		{
			    			System.out.println("Event handler failed. Re-publishing event: " + event.toString());
			    			try
			    			{
								eventQueue.publish(event);
							}
			    			catch (Exception e1)
			    			{
								e1.printStackTrace();
							}
			    		}
			    	}
                }
    		});
	    }
    }
}
