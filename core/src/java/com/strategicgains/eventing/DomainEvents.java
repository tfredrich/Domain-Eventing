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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * DomainEvents defines a static public interface for publishing and consuming domain events.
 * Raising an event pushes it to zero or more configured event buses to be handled asynchronously
 * by a subscribed EventHandler implementor.
 *
 * Domain events are publish-subscribe, where when an event is raised, all subscribed
 * EventHandler instances are notified of an event.
 * 
 * All raised DomainEvent instances are handled asynchronously.  However, they may NOT be published
 * asynchronously, depending on the underlying transport implementation.
 * 
 * @author toddf
 * @since May 12, 2011
 */
public class DomainEvents
{
	private static final DomainEvents INSTANCE = new DomainEvents();
	
	private Map<String, EventBus> eventBuses = new LinkedHashMap<>();

	private DomainEvents()
	{
		super();
	}

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
	 *
	 * Event publishing can only occur after event busses are setup.
	 * 
	 * @param busName the name of a specific event bus.
	 * @param event the Object as an event to publish.
	 */
	public static void publish(String busName, Object event)
	{
		instance().publishEvent(busName, event);
	}

	/**
	 * Publish an event, passing it to applicable consumers asynchronously.
	 *
	 * Event publishing can only occur after event busses are setup.
	 * 
	 * @param event the Object as an event to publish.
	 */
	public static void publish(Object event)
	{
		instance().publishEvent(event);
	}

	/**
	 * Subscribe to events on a named event bus.
	 * 
	 * @param busName the name of a specific event bus.
	 * @param consumer the consumer to call when matching events appear on the bus.
	 * @return a Subscription indicating the subscription details.
	 */
	public static Subscription subscribe(String busName, Consumer consumer)
	{
		return instance().subscribeTo(busName, consumer);
	}

	/**
	 * Subscribe a consumer to all currently-registered event buses.
	 * 
	 * @param consumer the consumer to call when matching events appear on the registered buses.
	 * @return a Subscription instance.
	 */
	public static Collection<Subscription> subscribe(Consumer consumer)
	{
		return instance().subscribeTo(consumer);
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
	 * Register an event bus with the DomainEvents manager using the buses' fully-qualified
	 * classname as the name for the bus.
	 * 
	 * @param bus a newly-constructed EventBus instance.
	 * @return true if the name is unique and the event bus was added.  Otherwise, false.
	 */
	public static boolean addBus(EventBus bus)
	{
		return instance().addEventBus(bus.getClass().getName(), bus);
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
		instance().shutdownAll();
	}

	private boolean addEventBus(String name, EventBus bus)
	{
		if (!eventBuses.containsKey(name))
		{
			eventBuses.put(name, bus);
			return true;
		}
		
		return false;
	}
	
	private EventBus getEventBus(String name)
	{
		EventBus eventBus = eventBuses.get(name);

		if (eventBus == null)
		{
			throw new RuntimeException("Unknown event bus name: " + name);
		}

		return eventBus;
	}
	
	private boolean hasEventBusses()
	{
		return (eventBuses != null);
	}

	/**
	 * Raise an event on all event busses, passing it to applicable consumers asynchronously.
	 * 
	 * @param event
	 */
	private void publishEvent(Object event)
	{
		assert(hasEventBusses());

		for (EventBus eventBus : eventBuses.values())
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
		eventBus.publish(event);
	}

	/**
	 * Subscribe to events on a named event bus.
	 * 
	 * @param name the name of an event bus.
	 * @param consumer the consumer to call when event appear on the bus.
	 * @return a Subscription indicating the subscription details.
	 */
	private Subscription subscribeTo(String name, Consumer consumer)
	{
		assert(hasEventBusses());
		EventBus eventBus = getEventBus(name);
		return eventBus.subscribe(consumer);
	}

	/**
	 * Subscribe to events on all currently-registered event buses.
	 * 
	 * @param consumer the consumer to call when event appear on the bus.
	 * @return a collection of Subscription instances, one for each bus subscribed-to.
	 */
	private Collection<Subscription> subscribeTo(Consumer consumer)
	{
		assert(hasEventBusses());

		List<Subscription> subscriptions = new ArrayList<>(eventBuses.size());

		eventBuses.values().forEach(new java.util.function.Consumer<EventBus>()
		{
			@Override
			public void accept(EventBus bus)
			{
				subscriptions.add(bus.subscribe(consumer));				
			}
		});

		return subscriptions;
	}

	private void shutdownAll()
	{
		for (EventBus eventBus : eventBuses.values())
		{
			eventBus.shutdown();
		}
		
		eventBuses.clear();
	}
}
