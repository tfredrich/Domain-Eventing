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
import java.util.List;

/**
 * DomainEvents defines a static public interface for raising and handling domain events.
 * Raising an event places it in an in-memory queue that is then handled asynchronously
 * by an EventMonitor.
 * <p/>
 * Domain events are publish-subscribe, where when an event is raised, all appropriate
 * EventHandler instances are notified of that event.
 * <p/>
 * To utilize domain events in your applications:
 * <ol>
 * <li>Implement DomainEvent sub-class(es) to represent each event type.</li>
 * <li>Implement EventHandler sub-class(es) for processing the events, overriding the handles(Class)
 * method to describe which DomainEvent sub-types the handler can process.</li>
 * <li>DomainEvents.register(new <EventHandlerSubType>()); // In main() or startup for each EventHandler.</li>
 * <li>Call DomainEvents.raise(new DomainEventSubType()) wherever domain events should be raised.
 * </ol>
 * 
 * All raised DomainEvent instances are handled asynchronously, in a separate Thread, which is
 * the EventMonitor managed by DomainEvents.
 * 
 * @author toddf
 * @since May 12, 2011
 */
public class DomainEvents
{
	// SECTION: CONSTANTS

	private static final int DEFAULT_DOMAIN_EVENT_WORKERS = 1;
	private static final DomainEvents INSTANCE = new DomainEvents();
	private static final List<EventHandler> registeredHandlers = new ArrayList<EventHandler>();

	
	// SECTION: INSTANCE VARIABLES

	private EventMonitor[] eventMonitors;
	private EventQueue eventQueue = new EventQueue();
	private int eventWorkerCount = DEFAULT_DOMAIN_EVENT_WORKERS;
	private boolean isStarted = false;
	
	// SECTION: CONSTRUCTOR

	private DomainEvents()
	{
		super();
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
	public static void raise(Object event)
	{
		instance().raiseEvent(event);
	}

	/**
	 * When true, re-raise events when an exception happens in the event handler. Setting
	 * to false causes the event to get dropped when an exception happens in the event handler.
	 * The default is true.
	 * 
	 * @param value true to retry, false to ignore events that fail processing.
	 */
	public static void setReRaiseOnError(boolean value)
	{
		instance().setRetryOnError(value);
	}

	/**
	 * Return the number of EventMonitor threads that will be started when
	 * DomainEvents.startMonitoring() is called.
	 * 
	 * @return the number of EventMonitor threads
	 */
	public static int getEventMonitorCount()
	{
		return instance().getEventWorkerCount();
	}
	
	/**
	 * Set the number of EventMonitor threads to start when DomainEvents.startMonitoring()
	 * is called.
	 * 
	 * @param value the number of desired EventMonitor threads.
	 */
	public static void setEventMonitorCount(int value)
	{
		instance().setEventWorkerCount(value);
	}

	/**
	 * Instantiate and start the EventMonitor threads to begin processing DomainEvent messages.
	 * Must be performed before calling DomainEvents.raise()
	 */
	public static void startMonitoring()
	{
		instance().startEventMonitors();
	}

	/**
	 * Shutdown domain event handling.  Call during application shutdown.
	 */
	public static void stopMonitoring()
	{
		instance().stopEventMonitors();
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
		registeredHandlers.add(handler);
		instance().registerHandler(handler);
	}

	/**
	 * Remove an EventHandler from receiving notification when DomainEvent are raised.
	 * 
	 * @param handler the handler to remove from receiving notifications.
	 */
	public static void unregister(EventHandler handler)
	{
		registeredHandlers.remove(handler);
		instance().unregisterHandler(handler);
	}

	
	// SECTION: INSTANCE METHODS

	private void startEventMonitors()
	{
		eventMonitors = new EventMonitor[getEventWorkerCount()];
		
		for (int i = 0; i < getEventWorkerCount(); ++i)
		{
			eventMonitors[i] = new EventMonitor(eventQueue);
			eventMonitors[i].start();
		}
		
		isStarted = true;
		registerHandlers(registeredHandlers);
	}
	
	private void stopEventMonitors()
	{
		for (EventMonitor eventMonitor : eventMonitors)
		{
			eventMonitor.shutdown();
		}
		
		unregisterHandlers(registeredHandlers);
		isStarted = false;
	}

	/**
	 * Raise an event, passing it to applicable consumers synchronously.
	 * 
	 * @param event
	 */
	private void raiseEvent(Object event)
	{
		eventQueue.raise(event);
	}

	/**
	 * When true, re-raise events when an exception happens in the event handler. Setting
	 * to false causes the event to get dropped when an exception happens in the event handler.
	 * 
	 * @param value true to retry, false to ignore events that fail processing.
	 */
	private void setRetryOnError(boolean value)
	{
		for (EventMonitor eventMonitor : eventMonitors)
		{
			eventMonitor.setReRaiseOnError(value);
		}
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
		if (!isStarted) return;

		for (EventMonitor eventMonitor : eventMonitors)
		{
			eventMonitor.register(handler);
		}
	}

	/**
	 * Register an EventHandler for notification when DomainEvent are raised.
	 * <p/>
	 * Register all handlers *before* raising events as handlers get cached by which type(s) of events
	 * they handle upon raise.
	 * 
	 * @param handler
	 */
	public void unregisterHandler(EventHandler handler)
	{
		if (!isStarted) return;

		for (EventMonitor eventMonitor : eventMonitors)
		{
			eventMonitor.unregister(handler);
		}
	}
	
	private void registerHandlers(List<EventHandler> handlers)
	{
		assert(isStarted);

		for(EventHandler handler : handlers)
		{
			registerHandler(handler);
		}
	}
	
	private void unregisterHandlers(List<EventHandler> handlers)
	{
		assert(isStarted);

		for(EventHandler handler : handlers)
		{
			unregisterHandler(handler);
		}
	}
	
	private int getEventWorkerCount()
	{
		return eventWorkerCount;
	}
	
	private void setEventWorkerCount(int value)
	{
		this.eventWorkerCount = value;
	}
}
