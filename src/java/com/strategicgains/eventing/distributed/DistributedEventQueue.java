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
package com.strategicgains.eventing.distributed;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.strategicgains.eventing.EventQueue;

/**
 * Leverages Hazelcast to create a distrubuted EventQueue implementation
 * to support intra-cluster eventing.
 * 
 * @author toddf
 * @since Jun 27, 2012
 */
public class DistributedEventQueue
extends EventQueue
{
	public DistributedEventQueue(String queueName)
	{
		super(Hazelcast.getQueue(queueName));
	}

	public DistributedEventQueue(String queueName, Config config)
	{
		super(Hazelcast.init(config).getQueue(queueName));
	}

    @Override
    public void shutdown()
    {
    	Hazelcast.shutdownAll();
    }
}