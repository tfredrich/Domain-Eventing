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

import java.util.Collection;

/**
 * Defines the interface for objects that can process (handle) domain events
 * of particular type(s).
 * 
 * Implementations of this interface are registered with the EventBus via
 * a call to subscribe(EventHandler) on the event bus itself. There is no
 * processing of the event object before it is sent to the event handler.
 * 
 * Depending on the underlying implementation of the transport system for the
 * event bus, the act of subscribing may materialize a subscription in an external
 * messaging system.
 * 
 * @author toddf
 * @since May 12, 2011
 */
public interface EventHandler
{
	/**
	 * Process the given event. Called by the EventMonitor when an event occurs.
	 * 
	 * @param event
	 * @throws Exception if something goes wrong
	 */
	public void handle(Object event)
	throws Exception;

	/**
	 * Provides a list of event types that this EventHandler cares about.
	 * Only messages of the given types will be sent to this event handler.
	 * 
	 * It is possible to use class names (simple or fully-qualified), however,
	 * when using non-local event transports such as Kafka or RabbitMQ, these
	 * event types become topics to which subscriptions are registered. To these
	 * event types must match topic names in those transports.
	 * 
	 * @return
	 */
	public Collection<String> getHandledEventTypes();
}
