/*
    Copyright 2015, Strategic Gains, Inc.

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
package com.strategicgains.eventing.akka;

import java.util.ArrayList;
import java.util.List;

import com.strategicgains.eventing.AbstractEventBus;
import com.strategicgains.eventing.Consumer;
import com.strategicgains.eventing.Subscription;

import akka.actor.ActorSystem;

/**
 * @author tfredrich
 * @since Jul 13, 2015
 */
public class AkkaEventBus
extends AbstractEventBus
{
	public AkkaEventBus()
    {
		this(ActorSystem.create("AkkaDomainEventing"));
    }

	public AkkaEventBus(ActorSystem actorSystem)
    {
		super(new AkkaEventTransport(actorSystem));
    }

	public List<Subscription> subscribeAll(List<Consumer> handlers)
    {
		List<Subscription> subscriptions = new ArrayList<>(handlers.size());

		for (Consumer handler : handlers)
		{
			subscriptions.add(getTransport().subscribe(handler));
		}

		return subscriptions;
    }
}
