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
 * infrastructure. Note that there is no concept of 'topic' or 'queue'
 * here.
 * 
 * All publishing is using a publish-subscribe metaphor more in the
 * JMS 'topic' sense. There can always be multiple subscribers to an event.
 * 
 * Depending on the underlying implementation though, there may be
 * multiple event types on a single channel.
 * 
 * @author toddf
 * @since Oct 18, 2012
 */
public interface Transport
{
	public Registration register(Producer producer);

	public void unregister(Registration registration);

	/**
	 * Publish and event to this event transport. If the event is an
	 * {@link Event} implementation, the value returned from getEventType()
	 * becomes the topic name.
	 * 
	 * @param event the event instance.
	 * @return true if this transport is capabable of publishing events of this type. Otherwise, false.
	 */
	public boolean publish(Object event);

	/**
	 * Subscribe a consumer to this event transport for the given eventTypes.
	 * 
	 * @param consumer an event handler that implements the {@link Consumer} interface.
	 * @return a Subscription instance.
	 */
	public Subscription subscribe(Consumer consumer);

	/**
	 * Remove a subscription from the underlying event transport.
	 * 
	 * @param subscription a Subscription instance returned from subscribe()
	 */
	public void unsubscribe(Subscription subscription);

	/**
	 * Terminate event handling on the transport and free all consumed resources.
	 */
	public void shutdown();
}
