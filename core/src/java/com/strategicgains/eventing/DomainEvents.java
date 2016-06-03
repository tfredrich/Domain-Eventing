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

	private Map<String, EventChannel> channels = new LinkedHashMap<>();

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
	 * Publish an event to a named event channel.
	 * The name of the bus is assigned during a call to addChannel(String, EventChannel).
	 *
	 * Event publishing can only occur after transports are setup and added.
	 * 
	 * @param channelName the name of a specific event channel.
	 * @param event the Object as an event to publish.
	 */
	public static void publish(String channelName, Object event)
	{
		instance()._publish(channelName, event);
	}

	/**
	 * Publish an event, passing it to applicable consumers asynchronously.
	 *
	 * Event publishing can only occur after event busses are setup.
	 * 
	 * @param event the Object as an event to publish.
	 * @throws Exception 
	 */
	public static void publish(Object event)
	{
		instance()._publish(event);
	}

	/**
	 * Register an {@link EventChannel} with the DomainEvents manager.
	 * 
	 * @param name the transport name.  Must be unique within the DomainEvents manager.
	 * @param channel an EventChannel instance.
	 * @return true if the name is unique and the event bus was added.  Otherwise, false.
	 */
	public static boolean addChannel(String name, EventChannel channel)
	{
		return instance()._addChannel(name, channel);
	}

	/**
	 * Register an {@link EventChannel} with the DomainEvents manager using the channel's fully-qualified
	 * classname as the name.
	 * 
	 * @param channel an EventChannel instance.
	 * @return true if the name is unique and the transport was added.  Otherwise, false.
	 */
	public static boolean addChannel(EventChannel channel)
	{
		return instance()._addChannel(channel.getClass().getName(), channel);
	}
	
	/**
	 * Get a registered {@link EventChannel} by name.
	 * 
	 * @param name the name of a channel given at the time of calling addChannel(String, EventChannel).
	 * @return an Transport instance, or null if 'name' not found.
	 */
	public static EventChannel getChannel(String name)
	{
		return instance()._getChannel(name);
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

	private boolean _addChannel(String name, EventChannel channel)
	{
		if (!channels.containsKey(name))
		{
			channels.put(name, channel);
			return true;
		}
		
		return false;
	}
	
	private EventChannel _getChannel(String name)
	{
		EventChannel channel = channels.get(name);

		if (channel == null)
		{
			throw new RuntimeException("Unknown channel name: " + name);
		}

		return channel;
	}
	
	private boolean _hasChannels()
	{
		return (channels != null);
	}

	/**
	 * Raise an event on all event channels, passing it to applicable handlers asynchronously.
	 * 
	 * @param event
	 */
	private void _publish(Object event)
	{
		assert(_hasChannels());

		for (EventChannel channel : channels.values())
		{
			channel.publish(event);
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
		assert(_hasChannels());
		_getChannel(name).publish(event);
	}

	private void _shutdown()
	{
		for (EventChannel transport : channels.values())
		{
			transport.shutdown();
		}
		
		channels.clear();
	}
}
