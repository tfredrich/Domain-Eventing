/*
    Copyright 2012, Strategic Gains, Inc.

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

import java.util.HashSet;
import java.util.Set;

/**
 * An EventBus provides a transport layer to enable the publishing of and subscription to events. The events are POJOs or Event implementations.
 * Each event type becomes, essentially, a 'topic' to which there can be multiple subscribers. All subscribers will receive the event.
 * 
 * An event bus can be configured to only publish messages of certain types. This may be useful when multiple event buses are configured and
 * registered with DomainEvents. For example a 'local' bus that publishes only within the VM and a 'public' bus that publishes enterprise wide.
 * If the 'local' bus handles different types of messages that the 'public' bus, you can configure the buses to only publish applicable types
 * using the addPublishableEventType() method.
 * 
 * @author toddf
 * @since Jun 27, 2012
 */
public abstract class EventBus
{
	private EventTransport transport;
	private Set<String> publishableEventTypes = new HashSet<>();

	public EventBus(EventTransport transport)
	{
		super();
		this.transport = transport;
	}

	/**
	 * Adds an event type to the list of event types that are acceptable for this event bus to publish.
	 * By default all message types are allowed. Once a specific one is added, the event bus will only
	 * messages of the added type(s).
	 *  
	 * @param eventType an event type name to allow this event bus to publish.
	 * @return true if the event type was added, false if the event type already existed.
	 */
	public boolean addPublishableEventType(String eventType)
	{
		return publishableEventTypes.add(eventType);
	}

	/**
	 * Answers whether this event bus implementation is allowed to publish the given event type.
	 * 
	 * @param eventType
	 * @return true if this EventBus implementation can send messages of the given type.
	 */
	public boolean canPublish(String eventType)
	{
		if (publishableEventTypes.isEmpty()) return true;

		return publishableEventTypes.contains(eventType);
	}

	/**
	 * Publish the event, if this event bus can publish events of that type.
	 * 
	 * @param event an event to publish.
	 */
	public void publish(Object event)
	{
		if (!canPublish(Events.getEventType(event))) return;

		transport.publish(event);
	}

	/**
	 * Shutdown this event bus and release its resources.
	 */
	public void shutdown()
	{
		transport.shutdown();
	}

	/**
	 * Create a subscription to this event bus, providing an EventHandler implementation to process events.
	 * 
	 * @param handler an event handler implementation.
	 * @return true if the subscription was created successfully. Otherwise, false.
	 */
	public boolean subscribe(EventHandler handler)
	{
		return transport.subscribe(handler);
	}

	/**
	 * Removes the subscription to this event bus for the given EventHandler.
	 * 
	 * @param handler an event handler implementation.
	 * @return true if the subscription was removedsuccessfully. Otherwise, false.
	 */
	public boolean unsubscribe(EventHandler handler)
	{
		return transport.unsubscribe(handler);
	}

	/**
	 * Get a handle to the underlying EventTransport implementation for this EventBus.
	 * 
	 * @return the EventTransport implementation on this event bus.
	 */
	protected EventTransport getTransport()
	{
		return transport;
	}
}
