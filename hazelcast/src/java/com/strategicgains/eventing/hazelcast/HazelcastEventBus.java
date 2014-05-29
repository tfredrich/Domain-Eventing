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

import java.io.Serializable;
import java.util.List;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.strategicgains.eventing.EventBus;
import com.strategicgains.eventing.EventHandler;

/**
 * Leverages Hazelcast to create a distrubuted EventBus implementation to
 * support intra-cluster eventing.
 * 
 * @author toddf
 * @since Jun 27, 2012
 */
public class HazelcastEventBus<T extends Serializable>
extends EventBus
{
	private HazelcastInstance hazelcast;

	public HazelcastEventBus(String queueName, List<EventHandler> subscribers)
	{
		this(queueName, new Config(), subscribers);
	}

	public HazelcastEventBus(String queueName, Config config, List<EventHandler> subscribers)
	{
		super(new HazelcastEventTransport());
		hazelcast = Hazelcast.newHazelcastInstance(config);
		((HazelcastEventTransport) getTransport()).setTopic(hazelcast.getTopic(queueName));
		addSubscribers(subscribers);
	}

    @Override
    public boolean subscribe(EventHandler handler)
    {
    	return getTransport().subscribe(handler);
    }

    @Override
    public boolean unsubscribe(EventHandler handler)
    {
    	return getTransport().unsubscribe(handler);
    }

	@Override
	public void shutdown()
	{
		Hazelcast.shutdownAll();
	}

	/**
	 * @param queueName the name of the event bus.
	 * @param subscribers a List of EventHandler instances that subscribed to the event bus.
	 */
	private void addSubscribers(List<EventHandler> subscribers)
	{
		for (EventHandler handler : subscribers)
		{
			getTransport().subscribe(handler);
		}
	}
}
