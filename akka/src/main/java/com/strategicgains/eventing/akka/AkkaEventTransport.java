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

import com.strategicgains.eventing.BaseSubscription;
import com.strategicgains.eventing.Consumer;
import com.strategicgains.eventing.Subscription;
import com.strategicgains.eventing.Transport;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.japi.ScanningEventBus;

/**
 * @author toddf
 * @since Jul 13, 2015
 */
public class AkkaEventTransport
implements Transport
{
	private ActorSystem system;
	private Map<Consumer, ActorRef> subscribers = new ConcurrentHashMap<Consumer, ActorRef>();
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
	public Subscription subscribe(Consumer consumer)
	{
		ActorRef adapter = system.actorOf(EventHandlerActor.props(consumer));
		akkaBus.subscribe(adapter, Object.class);
		subscribers.put(consumer, adapter);
		return new BaseSubscription(consumer);
	}

	@Override
	public void unsubscribe(Subscription subscription)
	{
		ActorRef adapter = subscribers.get(subscription.getConsumer());

		if (adapter != null)
		{
			akkaBus.unsubscribe(adapter);
			subscribers.remove(subscription.getConsumer());
		}
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
