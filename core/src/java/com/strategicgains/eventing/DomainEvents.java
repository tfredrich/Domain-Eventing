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
 * Consumer instances are notified of an event.
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

	private Map<String, Transport> transports = new LinkedHashMap<>();

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
	 * Event publishing can only occur after transports are setup and added.
	 * 
	 * @param transportName the name of a specific transport.
	 * @param event the Object as an event to publish.
	 */
	public static void publish(String transportName, Object event)
	{
		instance()._publish(transportName, event);
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
		instance()._publish(event);
	}

	/**
	 * Subscribe to events on a named {@link Transport}.
	 * 
	 * @param transportName the name of a specific event transport.
	 * @param consumer the consumer to call when matching events appear on the bus.
	 * @return a Subscription indicating the subscription details.
	 */
	public static Subscription subscribe(String transportName, Consumer consumer)
	{
		return instance()._subscribe(transportName, consumer);
	}

	/**
	 * Subscribe a consumer to all currently-registered transports.
	 * 
	 * @param consumer the consumer to call when matching events appear on the registered transports.
	 * @return a Subscription instance.
	 */
	public static Collection<Subscription> subscribe(Consumer consumer)
	{
		return instance()._subscribe(consumer);
	}

	/**
	 * Register a {@link Transport} with the DomainEvents manager.
	 * 
	 * @param name the transport name.  Must be unique within the DomainEvents manager.
	 * @param transport a newly-constructed Transport instance.
	 * @return true if the name is unique and the event bus was added.  Otherwise, false.
	 */
	public static boolean addTransport(String name, Transport transport)
	{
		return instance()._addTransport(name, transport);
	}

	/**
	 * Register a {@link Transport} with the DomainEvents manager using the transports' fully-qualified
	 * classname as the name.
	 * 
	 * @param transport a newly-constructed Transport instance.
	 * @return true if the name is unique and the transport was added.  Otherwise, false.
	 */
	public static boolean addTransport(Transport transport)
	{
		return instance()._addTransport(transport.getClass().getName(), transport);
	}
	
	/**
	 * Get a registered {@link Transport} by name.
	 * 
	 * @param name the name of a transport given at the time of calling addTransport(String, Transport).
	 * @return an Transport instance, or null if 'name' not found.
	 */
	public static Transport getTransport(String name)
	{
		return instance()._getTransport(name);
	}
	
	/**
	 * Shutdown all the event transports, releasing their resources cleanly.
	 * 
	 * shutdown() should be called at application termination to cleanly release
	 * all consumed resources.
	 */
	public static void shutdown()
	{
		instance()._shutdown();
	}

	private boolean _addTransport(String name, Transport transport)
	{
		if (!transports.containsKey(name))
		{
			transports.put(name, transport);
			return true;
		}
		
		return false;
	}
	
	private Transport _getTransport(String name)
	{
		Transport eventBus = transports.get(name);

		if (eventBus == null)
		{
			throw new RuntimeException("Unknown event bus name: " + name);
		}

		return eventBus;
	}
	
	private boolean _hasTransports()
	{
		return (transports != null);
	}

	/**
	 * Raise an event on all event transports, passing it to applicable consumers asynchronously.
	 * 
	 * @param event
	 */
	private void _publish(Object event)
	{
		assert(_hasTransports());

		for (Transport transport : transports.values())
		{
			transport.publish(event);
		}
	}

	/**
	 * Raise an event on a named event bus, passing it to applicable consumers asynchronously.
	 * 
	 * @param name the name of an event bus, assigned during calls to addEventBus(String, Transport).
	 * @param event the event to publish.
	 */
	private void _publish(String name, Object event)
	{
		assert(_hasTransports());
		Transport eventBus = _getTransport(name);
		eventBus.publish(event);
	}

	/**
	 * Subscribe to events on a named event bus.
	 * 
	 * @param transportName the name of an event bus.
	 * @param consumer the consumer to call when event appear on the bus.
	 * @return a Subscription indicating the subscription details.
	 */
	private Subscription _subscribe(String transportName, Consumer consumer)
	{
		assert(_hasTransports());
		Transport eventBus = _getTransport(transportName);
		return eventBus.subscribe(consumer);
	}

	/**
	 * Subscribe to events on all currently-registered event buses.
	 * 
	 * @param consumer the consumer to call when event appear on the bus.
	 * @return a collection of Subscription instances, one for each bus subscribed-to.
	 */
	private Collection<Subscription> _subscribe(Consumer consumer)
	{
		assert(_hasTransports());

		List<Subscription> subscriptions = new ArrayList<>(transports.size());

		transports.values().forEach(new java.util.function.Consumer<Transport>()
		{
			@Override
			public void accept(Transport bus)
			{
				subscriptions.add(bus.subscribe(consumer));				
			}
		});

		return subscriptions;
	}

	private void _shutdown()
	{
		for (Transport transport : transports.values())
		{
			transport.shutdown();
		}
		
		transports.clear();
	}
}
