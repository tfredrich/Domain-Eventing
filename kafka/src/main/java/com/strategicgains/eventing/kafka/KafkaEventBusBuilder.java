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
import com.strategicgains.eventing.Consumer;
import com.strategicgains.eventing.TransportBuilder;

/**
 * @author tfredrich
 * @since 20 May 2016
 */
public class KafkaEventBusBuilder
implements TransportBuilder<KafkaEventBus, KafkaEventBusBuilder>
{
	private Properties config;
	private ObjectMapper mapper;
	private Set<Consumer> consumers = new LinkedHashSet<>();

	public KafkaEventBusBuilder configuration(Properties props)
	{
		this.config = props;
		return this;
	}

	public KafkaEventBusBuilder jsonMapper(ObjectMapper mapper)
	{
		this.mapper = mapper;
		return this;
	}

	@Override
	public KafkaEventBusBuilder subscribe(Consumer consumer)
	{
		consumers.add(consumer);
		return this;
	}

	@Override
	public KafkaEventBusBuilder unsubscribe(Consumer consumer)
	{
		consumers.remove(consumer);
		return this;
	}

	@Override
	public KafkaEventBus build()
	{
		KafkaEventBus bus = new KafkaEventBus(config, topic, mapper);
		// TODO Auto-generated method stub
		return null;
	}
}
