/*
 * Copyright 2011, Strategic Gains, Inc.  All rights reserved.
 */
package com.strategicgains.eventing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.strategicgains.eventing.domain.DomainEvent;

/**
 * @author toddf
 * @since May 12, 2011
 */
public class DomainEvents
{
	// SECTION: CONSTANTS

	private static final DomainEvents INSTANCE = new DomainEvents();

	
	// SECTION: INSTANCE VARIABLES
	
	private Map<Class<? extends DomainEvent>, List<EventConsumer>> consumersByEvent = new HashMap<Class<? extends DomainEvent>, List<EventConsumer>>();
	private List<EventConsumer> consumers = new ArrayList<EventConsumer>();

	
	// SECTION: CONSTRUCTOR

	private DomainEvents()
	{
	}


	// SECTION: STATIC METHODS

	/**
	 * Get the Singleton instance of DomainEvents.
	 */
	public static DomainEvents instance()
	{
		return INSTANCE;
	}

	/**
	 * Raise an event, passing it to applicable consumers synchronously.  This method is
	 * equivalent to calling instance().raiseEvent(DomainEvent).
	 * 
	 * @param event the DomainEvent to raise.
	 */
	public static void raise(DomainEvent event)
	{
		instance().raiseEvent(event);
	}

	/**
	 * Register an EventConsumer for notification when DomainEvent are raised.
	 * <p/>
	 * Register all consumers *before* raising events as consumers get cached by which type(s) of events
	 * they handle upon raise.
	 * <p/>
	 * This method is equivalent to calling instance().registerConsumer(EventConsumer).
	 * 
	 * @param consumer
	 */
	public static void register(EventConsumer consumer)
	{
		instance().registerConsumer(consumer);
	}

	
	// SECTION: INSTANCE METHODS

	/**
	 * Raise an event, passing it to applicable consumers synchronously.
	 * 
	 * @param event
	 */
	public void raiseEvent(DomainEvent event)
	{
		for (EventConsumer consumer : getConsumersFor(event.getClass()))
		{
			consumer.receive(event);
		}
	}

	/**
	 * Register an EventConsumer for notification when DomainEvent are raised.
	 * <p/>
	 * Register all consumers *before* raising events as consumers get cached by which type(s) of events
	 * they handle upon raise.
	 * 
	 * @param consumer
	 */
	public void registerConsumer(EventConsumer consumer)
	{
		if (!consumers.contains(consumer))
		{
			consumers.add(consumer);
		}
		
		// Clear the cache.
		consumersByEvent.clear();
	}
	
	private List<EventConsumer> getConsumersFor(Class<? extends DomainEvent> eventClass)
	{
		List<EventConsumer> result = consumersByEvent.get(eventClass);
		
		if (result == null)
		{
			result = new ArrayList<EventConsumer>();
			consumersByEvent.put(eventClass, result);
			
			for (EventConsumer consumer : consumers)
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
