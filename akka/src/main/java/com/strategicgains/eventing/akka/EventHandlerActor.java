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

import com.strategicgains.eventing.Consumer;
import com.strategicgains.eventing.Events;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;

/**
 * @author tfredrich
 * @since Jul 13, 2015
 */
public class EventHandlerActor
extends UntypedActor
{
	private Consumer handler;

	public EventHandlerActor(Consumer handler)
	{
		super();
		this.handler = handler;
	}

	@Override
	public void onReceive(final Object event)
	throws Exception
	{
		if (event != null && handler.getConsumedEventTypes().contains(Events.getEventType(event)))
		{
			getContext().dispatcher().execute(new Runnable()
			{
				@Override
                public void run()
                {
					try
                    {
	                    handler.consume(event);
                    }
                    catch (Exception e)
                    {
	                    e.printStackTrace();
                    }
                }
			});
		}
	}

	public static Props props(final Consumer handler)
	{
		return Props.create(new ActorFactory(handler));
	}

	private static class ActorFactory
	implements Creator<EventHandlerActor>
	{
		private static final long serialVersionUID = -1142009288324369918L;

		private Consumer handler;

		public ActorFactory(Consumer handler)
		{
			super();
			this.handler = handler;
		}

		@Override
		public EventHandlerActor create() throws Exception
		{
			return new EventHandlerActor(handler);
		}
	}
}
