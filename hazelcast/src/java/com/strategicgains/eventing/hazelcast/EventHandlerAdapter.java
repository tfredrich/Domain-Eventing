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
package com.strategicgains.eventing.hazelcast;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.strategicgains.eventing.EventHandler;
import com.strategicgains.eventing.routing.SelectiveEventHandler;

/**
 * @author toddf
 * @since Oct 5, 2012
 */
public class EventHandlerAdapter
implements MessageListener<Object>
{
	// SECTION: CONSTANTS

	private static final Executor EVENT_EXECUTOR = Executors.newCachedThreadPool();

	// SECTION: INSTANCE VARIABLES

	private EventHandler handler;
	private boolean isSelectiveHandler;

	public EventHandlerAdapter(EventHandler handler)
	{
		super();
		this.handler = handler;
		isSelectiveHandler = (SelectiveEventHandler.class.isAssignableFrom(handler.getClass()));
	}

	@Override
	public void onMessage(Message<Object> message)
	{
		// TODO: implement logging
//		System.out.println("Message received: " + message.toString());

		if (shouldHandle(message.getMessageObject()))
		{
			processEvent(message.getMessageObject());
		}
	}

	private boolean shouldHandle(Object messageObject)
	{
		if (isSelectiveHandler)
		{
			return ((SelectiveEventHandler) handler).isSelected(messageObject);
		}

		return true;
	}

	private void processEvent(final Object event)
	{
		System.out.println("Processing event: " + event.toString());

		EVENT_EXECUTOR.execute(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					handler.handle(event);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
}
