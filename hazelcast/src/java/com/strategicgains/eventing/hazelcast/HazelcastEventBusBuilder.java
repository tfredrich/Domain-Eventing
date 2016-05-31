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
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.hazelcast.config.Config;
import com.strategicgains.eventing.TransportBuilder;
import com.strategicgains.eventing.Consumer;

/**
 * @author toddf
 * @since Oct 5, 2012
 */
public class HazelcastEventBusBuilder<T extends Serializable>
implements TransportBuilder<HazelcastEventBus<T>, HazelcastEventBusBuilder<T>>
{
	private static final String DEFAULT_QUEUE_NAME = "domain-events";

	private Config config = null;
	private String queueName = DEFAULT_QUEUE_NAME;
	private Set<Consumer> subscribers = new LinkedHashSet<Consumer>();

	public HazelcastEventBusBuilder()
	{
		super();
	}

	/**
	 * Very 'thin' (as in not at all) veneer to set underlying Hazelcast
	 * configuration. Yes, this exposes the underlying implementation. Bummer.
	 * 
	 * @param configuration Hazelcast Config instance.
	 * @return this builder to facilitate method chainging.
	 */
	public HazelcastEventBusBuilder<T> setConfiguration(Config configuration)
	{
		this.config = configuration;
		return this;
	}

	@Override
	public HazelcastEventBusBuilder<T> subscribe(Consumer handler)
	{
		subscribers.add(handler);
		return this;
	}

	@Override
	public HazelcastEventBusBuilder<T> unsubscribe(Consumer handler)
	{
		subscribers.remove(handler);
		return this;
	}

	@Override
	public HazelcastEventBus<T> build()
	{
		List<Consumer> subscriberList = Arrays.asList(subscribers.toArray(new Consumer[0]));
		return (config == null ? new HazelcastEventBus<T>(queueName, subscriberList) : new HazelcastEventBus<T>(queueName, config, subscriberList));
	}
}
