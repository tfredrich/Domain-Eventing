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

import java.util.LinkedHashSet;
import java.util.Set;

import com.hazelcast.config.Config;
import com.strategicgains.eventing.EventChannelBuilder;
import com.strategicgains.eventing.EventHandler;

/**
 * @author toddf
 * @since Oct 5, 2012
 */
public class HazelcastEventChannelBuilder
implements EventChannelBuilder<HazelcastEventChannel, HazelcastEventChannelBuilder>
{
	private static final String DEFAULT_TOPIC_NAME = "domain-events";

	private Config config = null;
	private String topicName = DEFAULT_TOPIC_NAME;
	private Set<EventHandler> handlers = new LinkedHashSet<>();

	public HazelcastEventChannelBuilder()
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
	public HazelcastEventChannelBuilder withConfiguration(Config configuration)
	{
		this.config = configuration;
		return this;
	}

	public HazelcastEventChannelBuilder withTopic(String topicName)
	{
		this.topicName = topicName;
		return this;
	}

	@Override
	public HazelcastEventChannelBuilder subscribe(EventHandler handler)
	{
		handlers.add(handler);
		return this;
	}

	@Override
	public HazelcastEventChannelBuilder unsubscribe(EventHandler handler)
	{
		handlers.remove(handler);
		return this;
	}

	@Override
	public HazelcastEventChannel build()
	{
		if (config == null) config = new Config();

		return new HazelcastEventChannel(config, topicName, handlers.toArray(new EventHandler[0]));
	}
}
