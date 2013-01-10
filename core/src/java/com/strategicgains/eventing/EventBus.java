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
 * @author toddf
 * @since Jun 27, 2012
 */
public abstract class EventBus
{
	private EventTransport transport;
	private Set<Class<?>> publishableEventTypes = new HashSet<Class<?>>();

	public EventBus(EventTransport transport)
	{
		super();
		this.transport = transport;
	}

	public boolean addPublishableEventType(Class<?> eventType)
	{
		return publishableEventTypes.add(eventType);
	}

	public boolean canPublish(Class<?> eventType)
	{
		if (publishableEventTypes.isEmpty()) return true;

		return publishableEventTypes.contains(eventType);
	}

	public void publish(Object event)
	{
		if (!canPublish(event.getClass())) return;

		transport.publish(event);
	}

	public void shutdown()
	{
		transport.shutdown();
	}

	public boolean subscribe(EventHandler handler)
	{
		return transport.subscribe(handler);
	}

	public boolean unsubscribe(EventHandler handler)
	{
		return transport.unsubscribe(handler);
	}
	
	protected EventTransport getTransport()
	{
		return transport;
	}
}