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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.strategicgains.eventing.EventHandler;
import com.strategicgains.eventing.EventQueue;

/**
 * A thread that allows clients to raise events.  Registered event handlers
 * will be called for whatever event types each can process.
 * 
 * @author toddf
 * @since May 17, 2011
 */
public class EventMonitor
extends Thread
{
	// SECTION: CONSTANTS

	private static final Executor eventExecutor = Executors.newCachedThreadPool();

	
	// SECTION: INSTANCE METHODS

	private Map<Class<?>, List<EventHandler>> handlersByEvent = new HashMap<Class<?>, List<EventHandler>>();
	private List<EventHandler> handlers = new ArrayList<EventHandler>();
	private boolean shouldShutDown = false;
	private boolean shouldReRaiseOnError = true;
	private EventQueue eventQueue;
	private long delay;


	// SECTION: CONSTRUCTORS

	public EventMonitor(EventQueue queue, long pollDelayMillis)
	{
		super();
		setDaemon(true);
		this.delay = pollDelayMillis;
		this.eventQueue = queue;
	}

	
	// SECTION: INSTANCE METHODS

	public synchronized void register(EventHandler handler)
	{
		if (!handlers.contains(handler))
		{
			handlers.add(handler);
		}
		
		handlersByEvent.clear();
	}

	public synchronized boolean unregister(EventHandler handler)
	{
		if (handlers.remove(handler))
		{
			handlersByEvent.clear();
			return true;
		}
		
		return false;
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
				System.out.println("Interrupted (use shutdown() to terminate).  Continuing...");
				continue;
			}

			Object event = null;

			while ((event = eventQueue.poll()) != null)
			{
				processEvent(event);
			}
		}
		
		System.out.println("Event monitor exiting...");
		clearAllHandlers();
	}

	private void processEvent(final Object event)
    {
	    System.out.println("Processing event: " + event.toString());
	    for (final EventHandler handler : getConsumersFor(event.getClass()))
	    {
    		eventExecutor.execute(new Runnable(){
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
			    			eventQueue.raise(event);
			    		}
			    	}
                }
    		});
	    }
    }

	
	// SECTION: UTILITY - PRIVATE

	private void clearAllHandlers()
    {
	    handlers.clear();
		handlersByEvent.clear();
    }

	private synchronized List<EventHandler> getConsumersFor(Class<?> eventClass)
	{
		List<EventHandler> result = handlersByEvent.get(eventClass);
		
		if (result == null)
		{
			result = new ArrayList<EventHandler>();
			handlersByEvent.put(eventClass, result);
			
			for (EventHandler consumer : handlers)
			{
				if (consumer.handles(eventClass))
				{
					result.add(consumer);
				}
			}
		}

		return result;
	}
}
