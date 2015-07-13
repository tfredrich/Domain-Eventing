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

import java.util.LinkedHashSet;
import java.util.Set;

import akka.actor.ActorSystem;

import com.strategicgains.eventing.EventHandler;
import com.strategicgains.eventing.EventTransport;

/**
 * @author toddf
 * @since Jul 13, 2015
 */
public class AkkaEventTransport
implements EventTransport
{
	private ActorSystem system;
	private Set<EventHandler> subscribers = new LinkedHashSet<EventHandler>();

	public AkkaEventTransport(ActorSystem actorSystem)
    {
		super();
		this.system = actorSystem;
    }

	@Override
	public void publish(Object event)
	{
		system.eventStream().publish(event);
	}

	@Override
	public boolean subscribe(EventHandler handler)
	{
//		system.eventStream().subscribe(subscriber, channel);
		return subscribers.add(handler);
	}

	@Override
	public boolean unsubscribe(EventHandler handler)
	{
//		system.eventStream().unsubscribe(subscriber);
		return subscribers.remove(handler);
	}

	@Override
	public void shutdown()
	{
		system.shutdown();
	}
}
