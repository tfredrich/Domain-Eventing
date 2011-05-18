/*
 * Copyright 2011, Pearson eCollege.  All rights reserved.
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
	private List<EventHandler> handlers;
	private boolean shouldShutDown = false;
	private Queue<DomainEvent> eventQueue;
	private long delay;

	
	// SECTION: CONSTANTS

	public EventMonitor(List<EventHandler> consumers)
	{
		this(consumers, DEFAULT_DELAY);
	}

	public EventMonitor(List<EventHandler> consumers, long pollDelayMillis)
	{
		super();
		setDaemon(true);
		this.handlers = new ArrayList<EventHandler>(consumers);
		this.delay = pollDelayMillis;
		this.eventQueue = new ConcurrentLinkedQueue<DomainEvent>();
	}

	
	// SECTION: INSTANCE METHODS

	public synchronized void shutdown()
	{
		shouldShutDown = true;
		System.out.println("Event monitor notified for shutdown.");
		notify();
	}

	public void raise(DomainEvent event)
	{
		System.out.println("Raising event: " + event.toString());
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
				System.out.println("Processing event: " + event.toString());
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
