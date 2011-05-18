/*
 * Copyright 2011, Strategic Gains, Inc.  All rights reserved.
 */
package com.strategicgains.eventing;

import java.util.ArrayList;
import java.util.List;

import com.strategicgains.eventing.domain.DomainEvent;

/**
 * DomainEvents defines a static public interface for raising and handling domain events.
 * Raising an event places it in an in-memory queue that is then handled asynchronously.
 * 
 * @author toddf
 * @since May 12, 2011
 */
public class DomainEvents
{
	// SECTION: CONSTANTS

	private static final DomainEvents INSTANCE = new DomainEvents();

	
	// SECTION: INSTANCE VARIABLES
	
	private EventMonitor eventMonitor;
	private List<EventHandler> eventHandlers = new ArrayList<EventHandler>();

	
	// SECTION: CONSTRUCTOR

	private DomainEvents()
	{
		super();
		this.eventMonitor = new EventMonitor(eventHandlers);
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

	public static void startMonitoring()
	{
		instance().startEventMonitor();
	}

	public static void stopMonitoring()
	{
		instance().stopEventMonitor();
	}

	/**
	 * Register an EventHandler for notification when DomainEvent are raised.
	 * <p/>
	 * Register all consumers *before* raising events as consumers get cached by which type(s) of events
	 * they handle upon raise.
	 * <p/>
	 * This method is equivalent to calling instance().registerConsumer(EventConsumer).
	 * 
	 * @param handler
	 */
	public static void register(EventHandler handler)
	{
		instance().registerHandler(handler);
	}

	
	// SECTION: INSTANCE METHODS

	private void startEventMonitor()
	{
		eventMonitor.start();
	}
	
	private void stopEventMonitor()
	{
		eventMonitor.shutdown();
	}

	/**
	 * Raise an event, passing it to applicable consumers synchronously.
	 * 
	 * @param event
	 */
	private void raiseEvent(DomainEvent event)
	{
		eventMonitor.raise(event);
	}

	/**
	 * Register an EventHandler for notification when DomainEvent are raised.
	 * <p/>
	 * Register all handlers *before* raising events as handlers get cached by which type(s) of events
	 * they handle upon raise.
	 * 
	 * @param handler
	 */
	public void registerHandler(EventHandler handler)
	{
		if (!eventHandlers.contains(handler))
		{
			eventHandlers.add(handler);
		}
	}
}
