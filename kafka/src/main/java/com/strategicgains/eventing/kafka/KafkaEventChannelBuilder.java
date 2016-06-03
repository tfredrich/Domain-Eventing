/*
    Copyright 2016, Ping Identity Corporation

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
package com.strategicgains.eventing.kafka;

import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strategicgains.eventing.EventChannelBuilder;
import com.strategicgains.eventing.EventHandler;

/**
 * @author tfredrich
 * @since 20 May 2016
 */
public class KafkaEventChannelBuilder
implements EventChannelBuilder<KafkaEventChannel, KafkaEventChannelBuilder>
{
	private Properties config;
	private ObjectMapper mapper;
	private Set<EventHandler> handlers = new LinkedHashSet<>();
	private String topic;

	public KafkaEventChannelBuilder configuration(Properties props)
	{
		this.config = props;
		return this;
	}

	public KafkaEventChannelBuilder jsonMapper(ObjectMapper mapper)
	{
		this.mapper = mapper;
		return this;
	}

	public KafkaEventChannelBuilder withTopic(String topic)
	{
		this.topic = topic;
		return this;
	}

	@Override
	public KafkaEventChannelBuilder subscribe(EventHandler handler)
	{
		handlers.add(handler);
		return this;
	}

	@Override
	public KafkaEventChannelBuilder unsubscribe(EventHandler handler)
	{
		handlers.remove(handler);
		return this;
	}

	@Override
	public KafkaEventChannel build()
	{
		return new KafkaEventChannel(config, topic, mapper, handlers.toArray(new EventHandler[0]));
	}
}
