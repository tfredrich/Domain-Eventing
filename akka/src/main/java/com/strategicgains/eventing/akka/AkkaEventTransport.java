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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.japi.ScanningEventBus;

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
	private Map<EventHandler, ActorRef> subscribers = new ConcurrentHashMap<EventHandler, ActorRef>();
	private AkkaBusImpl akkaBus;

	public AkkaEventTransport(ActorSystem actorSystem)
    {
		super();
		this.system = actorSystem;
		akkaBus = new AkkaBusImpl();
    }

	@Override
	public void publish(Object event)
	{
		akkaBus.publish(event);
	}

	@Override
	public boolean subscribe(EventHandler handler)
	{
		ActorRef adapter = system.actorOf(EventHandlerActor.props(handler));
		akkaBus.subscribe(adapter, Object.class);
		subscribers.put(handler, adapter);
		return true;
	}

	@Override
	public boolean unsubscribe(EventHandler handler)
	{
		ActorRef adapter = subscribers.get(handler);

		if (adapter != null)
		{
			akkaBus.unsubscribe(adapter);
			return (subscribers.remove(handler) != null);
		}

		return false;
	}

	@Override
	public void shutdown()
	{
		system.shutdown();
	}

	private class AkkaBusImpl
	extends ScanningEventBus<Object, ActorRef, Class<?>>
	{
		@Override
	    public int compareClassifiers(Class<?> a, Class<?> b)
	    {
		    return a.getName().compareTo(b.getName());
	    }

		@Override
	    public int compareSubscribers(ActorRef a, ActorRef b)
	    {
		    return a.compareTo(b);
	    }

		@Override
	    public boolean matches(Class<?> type, Object event)
	    {
		    return true;
	    }

		@Override
	    public void publish(Object event, ActorRef subscriber)
	    {
			subscriber.tell(event, ActorRef.noSender());
	    }
	}
}
