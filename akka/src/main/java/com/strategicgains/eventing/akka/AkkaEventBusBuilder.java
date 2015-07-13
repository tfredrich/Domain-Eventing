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

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import akka.actor.ActorSystem;

import com.strategicgains.eventing.EventBusBuilder;
import com.strategicgains.eventing.EventHandler;

/**
 * @author tfredrich
 * @since Jul 13, 2015
 */
public class AkkaEventBusBuilder
implements EventBusBuilder<AkkaEventBus, AkkaEventBusBuilder>
{
	private ActorSystem actorSystem;
	private Set<EventHandler> subscribers = new LinkedHashSet<EventHandler>();

	public AkkaEventBusBuilder()
	{
		super();
	}

	public AkkaEventBusBuilder actorSystem(ActorSystem actorSystem)
	{
		this.actorSystem = actorSystem;
		return this;
	}

	@Override
    public AkkaEventBusBuilder subscribe(EventHandler handler)
    {
		subscribers.add(handler);
	    return this;
    }

	@Override
    public AkkaEventBusBuilder unsubscribe(EventHandler handler)
    {
		subscribers.remove(handler);
	    return this;
    }

	@Override
    public AkkaEventBus build()
    {
		AkkaEventBus bus = (actorSystem == null ? new AkkaEventBus() : new AkkaEventBus(actorSystem));
		bus.subscribeAll(Arrays.asList(subscribers.toArray(new EventHandler[0])));
	    return bus;
    }
}
