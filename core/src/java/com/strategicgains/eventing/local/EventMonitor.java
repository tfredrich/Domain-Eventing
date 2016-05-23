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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.strategicgains.eventing.BaseSubscription;
import com.strategicgains.eventing.Consumer;
import com.strategicgains.eventing.Events;
import com.strategicgains.eventing.Subscription;

/**
 * A thread that receives published events and sends them to subscribers.
 * Registered event handlers will be called for whatever event types each can process.
 * 
 * EventHandlers are called using an Executor pool that grows dynamically as needed, so
 * they are run asynchronously.
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

	private Map<String, List<Consumer>> consumersByEvent = new ConcurrentHashMap<>();
	private Set<Consumer> handlers = new LinkedHashSet<Consumer>();
	private boolean shouldShutDown = false;
	private boolean shouldReRaiseOnError = true;
	private LocalEventTransport eventQueue;
	private long delay;


	// SECTION: CONSTRUCTORS

	public EventMonitor(LocalEventTransport queue, long pollDelayMillis)
	{
		super();
		setDaemon(true);
		this.delay = pollDelayMillis;
		this.eventQueue = queue;
	}

	
	// SECTION: INSTANCE METHODS

	public synchronized Subscription register(Consumer handler)
	{
		handlers.add(handler);
		consumersByEvent.clear();
		return new BaseSubscription(handler);
	}

	public synchronized boolean unregister(Consumer handler)
	{
		if (handlers.remove(handler))
		{
			consumersByEvent.clear();
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
		clearAllConsumers();
	}

	/**
	 * Runs each appropriate EventHandler in an Executor.
	 * 
	 * @param event
	 */
	private void processEvent(final Object event)
    {
	    System.out.println("Processing event: " + event.toString());
	    for (final Consumer handler : getConsumersFor(event))
	    {
    		EVENT_EXECUTOR.execute(new Runnable(){
				@Override
                public void run()
                {
			    	try
			    	{
			    		handler.consume(event);
			    	}
			    	catch(Exception e)
			    	{
			    		e.printStackTrace();
			    		
			    		if (shouldReRaiseOnError)
			    		{
			    			System.out.println("Event handler failed. Re-publishing event: " + event.toString());
			    			eventQueue.publish(event);
			    		}
			    	}
                }
    		});
	    }
    }

	
	// SECTION: UTILITY - PRIVATE

	private void clearAllConsumers()
    {
	    handlers.clear();
		consumersByEvent.clear();
    }

	private synchronized List<Consumer> getConsumersFor(Object event)
	{
		String eventType = Events.getEventType(event);
		List<Consumer> result = consumersByEvent.get(eventType);
		
		if (result == null)
		{
			result = new ArrayList<Consumer>();
			consumersByEvent.put(eventType, result);
			
			for (Consumer consumer : handlers)
			{
				if (consumer.getConsumedEventTypes().contains(eventType))
				{
					result.add(consumer);
				}
			}
		}

		return result;
	}
}
