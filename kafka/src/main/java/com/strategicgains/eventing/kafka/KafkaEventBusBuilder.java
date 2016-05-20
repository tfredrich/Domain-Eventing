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

import com.strategicgains.eventing.EventBusBuilder;
import com.strategicgains.eventing.EventHandler;

/**
 * @author tfredrich
 * @since 20 May 2016
 */
public class KafkaEventBusBuilder
implements EventBusBuilder<KafkaEventBus, KafkaEventBusBuilder>
{
	@Override
	public KafkaEventBusBuilder subscribe(EventHandler handler)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public KafkaEventBusBuilder unsubscribe(EventHandler handler)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public KafkaEventBus build()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
