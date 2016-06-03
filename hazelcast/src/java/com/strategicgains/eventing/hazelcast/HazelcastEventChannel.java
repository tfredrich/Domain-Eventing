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
package com.strategicgains.eventing.hazelcast;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.strategicgains.eventing.EventHandler;
import com.strategicgains.eventing.SubscribableEventChannel;

/**
 * @author toddf
 * @since Oct 18, 2012
 */
public class HazelcastEventChannel
implements SubscribableEventChannel
{
	private ITopic<Object> topic;
	private Map<EventHandler, String> subscriptions = new ConcurrentHashMap<>();

	public HazelcastEventChannel(String topicName, EventHandler... eventHandlers)
	{
		this(new Config(), topicName, eventHandlers);
	}

	public HazelcastEventChannel(Config config, String topicName, EventHandler... eventHandlers)
	{
		super();

		HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance(config);
		setTopic(hazelcast.getTopic(topicName));
		subscribeAll(eventHandlers);
	}

	private void subscribeAll(EventHandler... eventHandlers)
	{
		for (EventHandler handler : eventHandlers)
		{
			subscribe(handler);
		}
	}

	protected void setTopic(ITopic<Object> aTopic)
    {
		this.topic = aTopic;
    }

	@Override
	public boolean publish(Object event)
	{
		topic.publish(event);
		return true;
	}

	@Override
	public void shutdown()
	{
		topic.destroy();
	}

	@Override
	public boolean subscribe(EventHandler consumer)
	{
		String listenerId = topic.addMessageListener(new EventHandlerAdapter(consumer));
		subscriptions.put(consumer, listenerId);
		return true;
	}

	@Override
	public void unsubscribe(EventHandler handler)
	{
		String listenerId = subscriptions.get(handler);

		if (listenerId != null)
		{
			topic.removeMessageListener(listenerId);
		}
	}
}
