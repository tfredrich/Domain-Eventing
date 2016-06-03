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

import com.strategicgains.eventing.exception.EventingException;

/**
 * Defines the callback interface to handle messages from channel subscriptions.
 * 
 * Implementations of this interface are registered with an {@link EventChannel} via
 * a call to subscribe(EventHandler). There is no processing of the event object
 * before it is sent to the handler from the channel.
 * 
 * Depending on the underlying implementation of the transport system, the act of
 * subscribing may materialize a subscription in an external messaging system.
 * 
 * @author toddf
 * @since May 12, 2011
 */
public interface EventHandler
{
	/**
	 * Process the given event.
	 * 
	 * @param event an event or message.
	 * @throws EventingException if handler fails to process the event.
	 */
	public void handle(Object event)
	throws EventingException;
}
