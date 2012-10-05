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

import java.util.Queue;

/**
 * @author toddf
 * @since Jun 27, 2012
 */
public abstract class EventBus
{
	private Queue<Object> eventQueue;

	public EventBus(Queue<Object> queueImpl)
	{
		super();
		this.eventQueue = queueImpl;
	}

	public boolean isEmpty()
	{
		return eventQueue.isEmpty();
	}

	public Object poll()
	{
		return eventQueue.poll();
	}

	public void publish(Object event)
	{
		if (!canPublish(event.getClass())) return;

		eventQueue.add(event);

		synchronized (this)
		{
			notify();
		}
	}

	public boolean canPublish(Class<?> eventType)
	{
		return true;
	}

	public abstract void shutdown();
	public abstract boolean subscribe(EventHandler handler);
	public abstract boolean unsubscribe(EventHandler handler);
}