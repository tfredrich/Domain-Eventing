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
package com.strategicgains.eventing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.strategicgains.eventing.domain.DomainEvent;

/**
 * @author toddf
 * @since May 17, 2011
 */
public class EventMonitor
extends Thread
{
	private static final long DEFAULT_DELAY = 0L;

	
	// SECTION: INSTANCE METHODS

	private Map<Class<? extends DomainEvent>, List<EventHandler>> handlersByEvent = new HashMap<Class<? extends DomainEvent>, List<EventHandler>>();
	private List<EventHandler> handlers = new ArrayList<EventHandler>();
	private boolean shouldShutDown = false;
	private Queue<DomainEvent> eventQueue;
	private long delay;

	
	// SECTION: CONSTANTS

	public EventMonitor()
	{
		this(DEFAULT_DELAY);
	}

	public EventMonitor(long pollDelayMillis)
	{
		super();
		setDaemon(true);
		this.delay = pollDelayMillis;
		this.eventQueue = new ConcurrentLinkedQueue<DomainEvent>();
	}

	
	// SECTION: INSTANCE METHODS

	public void register(EventHandler handler)
	{
		if (!handlers.contains(handler))
		{
			handlers.add(handler);
		}
		
		handlersByEvent.clear();
	}

	public synchronized void shutdown()
	{
		shouldShutDown = true;
		System.out.println("Event monitor notified for shutdown.");
		notify();
	}

	public void raise(DomainEvent event)
	{
//		System.out.println("Raising event: " + event.toString());
		eventQueue.add(event);

		synchronized (this)
		{
			notify();
		}
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
				if (eventQueue.isEmpty())
				{
					synchronized (this)
					{
						wait(delay);
					}
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				System.out.println("Interruped (use shutdown() to terminate).  Continuing...");
				continue;
			}

			DomainEvent event = null;

			while ((event = eventQueue.poll()) != null)
			{
//				System.out.println("Processing event: " + event.toString());
				for (EventHandler handler : getConsumersFor(event.getClass()))
				{
					try
					{
						handler.handle(event);
					}
					catch(Exception e)
					{
						e.printStackTrace();
						System.out.println("Event handler failed. Re-publishing event: " + event.toString());
						raise(event);
					}
				}
			}
		}
		
		System.out.println("Event monitor exiting...");
	}

	
	// SECTION: UTILITY - PRIVATE

	private List<EventHandler> getConsumersFor(Class<? extends DomainEvent> eventClass)
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
