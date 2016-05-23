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
 * An event transport is an underlying implementation of a messaging
 * infrastructure. Note that there is no concept of 'topic' here. All
 * publishing is using a'topic concept with the assumption being that
 * there is 'one topic per event type'. This means that the event type
 * of every event getting published becomes essentially the topic name. 
 * 
 * @author toddf
 * @since Oct 18, 2012
 */
public interface Transport
{
	/**
	 * Publish and event to this event transport. If the event is an
	 * {@link Event} implementation, the value returned from getEventType()
	 * becomes the topic name.
	 * 
	 * @param event the event instance.
	 */
	public void publish(Object event);

	/**
	 * Subscribe a consumer to this event transport for the given eventTypes.
	 * 
	 * @param handler an event handler that implements the {@link Consumer} interface.
	 * @return a Subscription instance.
	 */
	public Subscription subscribe(Consumer handler);

	/**
	 * Remove a subscription from the underlying event transport.
	 * 
	 * @param subscription a Subscription instance returned from subscribe()
	 */
	public void unsubscribe(Subscription subscription);

	/**
	 * Terminate event handling on the transport and free all consumed
	 * resources.
	 */
	public void shutdown();
}
