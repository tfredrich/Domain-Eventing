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

/**
 * An event channel is an underlying implementation of a messaging
 * infrastructure. Note that there is no concept of 'topic' or 'queue'
 * here. A channel might equate to an topic though, depending on the
 * underlying implementation.
 * 
 * All publishing is using a publish-subscribe metaphor more in the
 * JMS 'topic' sense. There can always be multiple subscribers to a
 * channel.
 * 
 * Depending on the underlying implementation, there may be
 * multiple event types on a single channel.
 * 
 * @author toddf
 * @since Oct 18, 2012
 */
public interface EventChannel
{
	/**
	 * Publish an event to this event channel.
	 * 
	 * @param event the event instance.
	 * @return true if the event is published to the channel. Otherwise, false for non-fatal failure to publish reasons (for example, this channel cannot publish events of that type).
	 * @throws RuntimeException for non-recoverable errors.
	 */
	public boolean publish(Object event);

	/**
	 * Subscribe a consumer to this event transport for the given eventTypes.
	 * 
	 * @param handler an event handler that implements the {@link EventHandler} interface.
	 * @return true if the subscription was successful. Otherwise, false.
	 */
	public boolean subscribe(EventHandler handler);

	/**
	 * Remove a subscription from the underlying event transport.
	 * 
	 * @param handler an {@link EventHandler}
	 */
	public void unsubscribe(EventHandler handler);

	/**
	 * Terminate event handling on the transport and free all consumed resources.
	 */
	public void shutdown();
}
