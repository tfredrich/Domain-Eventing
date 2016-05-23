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

import com.hazelcast.core.ITopic;
import com.strategicgains.eventing.BaseSubscription;
import com.strategicgains.eventing.Consumer;
import com.strategicgains.eventing.Subscription;
import com.strategicgains.eventing.Transport;

/**
 * @author toddf
 * @since Oct 18, 2012
 */
public class HazelcastEventTransport
implements Transport
{
	private ITopic<Object> topic;
	private Map<Consumer, String> subscriptions = new ConcurrentHashMap<Consumer, String>();

	protected HazelcastEventTransport()
	{
		super();
	}

	public HazelcastEventTransport(ITopic<Object> topic)
	{
		this();
		setTopic(topic);
	}

	protected void setTopic(ITopic<Object> aTopic)
    {
		this.topic = aTopic;
    }

	@Override
	public void publish(Object event)
	{
		topic.publish(event);
	}

	@Override
	public void shutdown()
	{
		topic.destroy();
	}

	@Override
	public Subscription subscribe(Consumer consumer)
	{
		String listenerId = topic.addMessageListener(new EventHandlerAdapter(consumer));
		subscriptions.put(consumer, listenerId);
		return new BaseSubscription(consumer);
	}

	@Override
	public void unsubscribe(Subscription subscription)
	{
		String listenerId = subscriptions.get(subscription.getConsumer());

		if (listenerId != null)
		{
			topic.removeMessageListener(listenerId);
		}
	}
}
