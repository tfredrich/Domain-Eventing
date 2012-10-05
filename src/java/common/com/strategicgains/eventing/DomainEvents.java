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

import com.strategicgains.eventing.local.LocalEventBusBuilder;


/**
 * DomainEvents defines a static public interface for raising and handling domain events.
 * Raising an event places it in an in-memory queue that is then handled asynchronously
 * by an EventMonitor.
 * <p/>
 * Domain events are publish-subscribe, where when an event is raised, all appropriate
 * EventHandler instances are notified of an event, if they have subscribed and handles() returns
 * true for the given event class.
 * <p/>
 * To utilize domain events in your application:
 * <ol>
 * <li>Implement EventHandler sub-class(es) for processing the events, overriding the handles(Class)
 * method to describe which event types the handler can process.</li>
 * <li>DomainEvents.register(new <EventHandlerSubType>()); // In main() or startup for each EventHandler.</li>
 * <li>(optional) DomainEvents.useDistributedEvents(); // Before startMonitoring() called to support eventing within a cluster.
 * <li>DomainEvents.startMonitoring(); // In main() when ready to process events.
 * <li>Call DomainEvents.raise(new DomainEventSubType()) wherever domain events should be raised.
 * <li>DomainEvents.stopMonitoring(); // On application shutdown (e.g. in main()).
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
	 * Construct a new Local event bus builder.
	 * 
	 * @return LocalEventBusBuilder
	 */
	public static LocalEventBusBuilder newEventBusBuilder()
	{
		return new LocalEventBusBuilder();
	}

	/**
	 * Publish an event, passing it to applicable consumers asynchronously.  This method is
	 * equivalent to calling instance().publishEvent(Object).
	 * 
	 * @param event the Object as an event to publish.
	 */
	public static void publish(Object event)
	{
		instance().publishEvent(event);
	}

	public static boolean addBus(String name, EventBus bus)
	{
		return instance().addEventBus(name, bus);
	}
	
	public static EventBus getBus(String name)
	{
		return instance().getEventBus(name);
	}
	
	public static void shutdown()
	{
		instance().shutdownEventQueues();
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

	/**
	 * Raise an event, passing it to applicable consumers asynchronously.
	 * 
	 * @param event
	 */
	private void publishEvent(Object event)
	{
		assert(!eventBusses.isEmpty());

		for (EventBus eventQueue : eventBusses.values())
		{
			eventQueue.publish(event);
		}
	}

	private void shutdownEventQueues()
	{
		for (EventBus eventQueue : eventBusses.values())
		{
			eventQueue.shutdown();
		}
		
		eventBusses.clear();
	}
}
