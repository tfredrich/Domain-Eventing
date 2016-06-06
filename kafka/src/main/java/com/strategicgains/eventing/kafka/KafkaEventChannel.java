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

import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.strategicgains.eventing.EventHandler;
import com.strategicgains.eventing.SubscribableEventChannel;

/**
 * @author tfredrich
 * @since 20 May 2016
 */
public class KafkaEventChannel
implements SubscribableEventChannel
{
	private String topic;
	private Producer<String, String> producer;
	private ObjectMapper mapper;

	public KafkaEventChannel(Properties config, String topic, ObjectMapper mapper, EventHandler... eventHandlers)
	{
		this.producer = new KafkaProducer<>(config);
		this.topic = topic;
		this.mapper = mapper;
	}

	@Override
	public boolean publish(Object event)
	{
		try
		{
			Future<RecordMetadata> f = producer.send(new ProducerRecord<String, String>(topic, System.currentTimeMillis() + "", mapper.writeValueAsString(event)));
		}
		catch (JsonProcessingException e)
		{
			// TODO implement logging
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public boolean subscribe(EventHandler handler)
	{
		return false;
	}

	@Override
	public void unsubscribe(EventHandler handler)
	{
	}

	@Override
	public void shutdown()
	{
		producer.close();
	}
}
