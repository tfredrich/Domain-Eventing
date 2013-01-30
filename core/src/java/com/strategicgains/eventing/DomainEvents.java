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

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * DomainEvents defines a static public interface for raising and handling domain events.
 * Raising an event places it in an in-memory queue that is then handled asynchronously
 * by an EventMonitor.
 * <p/>
 * Domain events are publish-subscribe, where when an event is raised, all appropriate
 * EventHandler instances are notified of an event, if they have subscribed and handles() returns
 * true for the given event class.
 * 
 * All raised DomainEvent instances are handled asynchronously.  However, they may NOT be published
 * asynchronously, depending on the underlying transport implementation.
 * 
 * @author toddf
 * @since May 12, 2011
 */
public class DomainEvents
{
	// SECTION: CONSTANTS

	private static final DomainEvents INSTANCE = new DomainEvents();

	
	// SECTION: INSTANCE VARIABLES

	private Map<String, EventBus> eventBusses = new LinkedHashMap<String, EventBus>();


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
	 * Publish an event to a named event bus.
	 * The name of the bus is assigned during a call to addBus(String, EventBus).
	 * <p/>
	 * Event publishing can only occur after event busses are setup.
	 * 
	 * @param name the name of a specific event bus.
	 * @param event the Object as an event to publish.
	 */
	public static void publish(String name, Object event)
	{
		instance().publishEvent(name, event);
	}

	/**
	 * Publish an event, passing it to applicable consumers asynchronously.
	 * <p/>
	 * Event publishing can only occur after event busses are setup.
	 * 
	 * @param event the Object as an event to publish.
	 */
	public static void publish(Object event)
	{
		instance().publishEvent(event);
	}

	/**
	 * Register an event bus with the DomainEvents manager.
	 * 
	 * @param name the event bus name.  Must be unique within the DomainEvents manager.
	 * @param bus a newly-constructed EventBus instance.
	 * @return true if the name is unique and the event bus was added.  Otherwise, false.
	 */
	public static boolean addBus(String name, EventBus bus)
	{
		return instance().addEventBus(name, bus);
	}
	
	/**
	 * Get an event bus by name.
	 * 
	 * @param name the name of an event bus given at the time of calling addBus(String, EventBus).
	 * @return an EventBus instance, or null if 'name' not found.
	 */
	public static EventBus getBus(String name)
	{
		return instance().getEventBus(name);
	}
	
	/**
	 * Shutdown all the even busses, releasing their resources cleanly.
	 * <p/>
	 * shutdown() should be called at application termination to cleanly release
	 * all consumed resources.
	 */
	public static void shutdown()
	{
		instance().shutdownEventBusses();
	}


	// SECTION: INSTANCE METHODS

	private boolean addEventBus(String name, EventBus bus)
	{
		if (!eventBusses.containsKey(name))
		{
			eventBusses.put(name, bus);
			return true;
		}
		
		return false;
	}
	
	private EventBus getEventBus(String name)
	{
		return eventBusses.get(name);
	}
	
	private boolean hasEventBusses()
	{
		return (eventBusses != null);
	}

	/**
	 * Raise an event on all event busses, passing it to applicable consumers asynchronously.
	 * 
	 * @param event
	 */
	private void publishEvent(Object event)
	{
		assert(hasEventBusses());

		for (EventBus eventBus : eventBusses.values())
		{
			eventBus.publish(event);
		}
	}

	/**
	 * Raise an event on a named event bus, passing it to applicable consumers asynchronously.
	 * 
	 * @param name the name of an event bus, assigned during calls to addEventBus(String, EventBus).
	 * @param event the event to publish.
	 */
	private void publishEvent(String name, Object event)
	{
		assert(hasEventBusses());
		EventBus eventBus = getEventBus(name);
		
		if (eventBus == null)
		{
			throw new RuntimeException("Unknown event bus name: " + name);
		}

		eventBus.publish(event);
	}

	private void shutdownEventBusses()
	{
		for (EventBus eventBus : eventBusses.values())
		{
			eventBus.shutdown();
		}
		
		eventBusses.clear();
	}
}
