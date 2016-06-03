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
package com.strategicgains.eventing.local;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.strategicgains.eventing.EventHandler;
import com.strategicgains.eventing.simple.SimpleEventChannel;

/**
 * @author toddf
 * @since Oct 5, 2012
 */
public class SimpleEventChannelTest
{
	private static final int PAUSE_MILLIS = 500;
	private TestEventHandler handler1 = new TestEventHandler();
	private TestEventHandler handler2 = new TestEventHandler();
	private TestLongEventHandler handler3 = new TestLongEventHandler();
	private SimpleEventChannel channel;

	@Before
	public void setup()
	{
		channel = new SimpleEventChannel(handler1, handler2, handler3);
	}
	
	@After
	public void teardown()
	{
		channel.shutdown();
	}

	@Test
	public void shouldUseSeparateChannel()
	throws Exception
	{
		TestEventHandler otherHandler = new TestEventHandler();
		SimpleEventChannel otherChannel = new SimpleEventChannel(otherHandler);

		assertEquals(0, handler1.getCallCount());
		assertEquals(0, handler2.getCallCount());
		assertEquals(0, handler3.getCallCount());
		assertEquals(0, otherHandler.getCallCount());
		channel.publish(new NormalEvent());
		otherChannel.publish(new NormalEvent());
		Thread.sleep(PAUSE_MILLIS);
		assertEquals(1, handler1.getCallCount());
		assertEquals(1, handler2.getCallCount());
		assertEquals(1, handler3.getCallCount());
		assertEquals(1, otherHandler.getCallCount());
	}

	@Test
	public void shouldNotifyAllSubscribers()
	throws Exception
	{
		assertEquals(0, handler1.getCallCount());
		assertEquals(0, handler2.getCallCount());
		assertEquals(0, handler3.getCallCount());
		channel.publish(new NormalEvent());
		Thread.sleep(PAUSE_MILLIS);
		assertEquals(1, handler1.getCallCount());
		assertEquals(1, handler2.getCallCount());
		assertEquals(1, handler3.getCallCount());
	}

	@Test
	public void shouldNotifyEventHandlerMultipleTimes()
	throws Exception
	{
		assertEquals(0, handler1.getCallCount());
		assertEquals(0, handler2.getCallCount());
		assertEquals(0, handler3.getCallCount());
		channel.publish(new NormalEvent());
		channel.publish(new ErroredEvent());
		channel.publish(new NormalEvent());
		channel.publish(new ErroredEvent());
		channel.publish(new NormalEvent());
		channel.publish(new ErroredEvent());
		channel.publish(new NormalEvent());
		channel.publish(new ErroredEvent());
		channel.publish(new NormalEvent());
		channel.publish(new ErroredEvent());
		Thread.sleep(PAUSE_MILLIS);
		assertEquals(10, handler1.getCallCount());
		assertEquals(10, handler2.getCallCount());
		assertEquals(10, handler3.getCallCount());
	}

	@Test
	public void shouldRetryEventHandler()
	throws Exception
	{
		channel.retryOnError(true);

		assertEquals(0, handler1.getCallCount());
		channel.publish(new ErroredEvent());
		Thread.sleep(PAUSE_MILLIS);
		assertEquals(6, handler1.getCallCount());
		assertEquals(6, handler2.getCallCount());
		assertEquals(6, handler3.getCallCount());
	}

	@Test
	public void shouldNotRetryEventHandler()
	throws Exception
	{
		assertEquals(0, handler1.getCallCount());
		channel.publish(new ErroredEvent());
		Thread.sleep(PAUSE_MILLIS);
		assertEquals(1, handler1.getCallCount());
		assertEquals(1, handler2.getCallCount());
		assertEquals(1, handler3.getCallCount());
	}

	@Test
	public void shouldProcessInParallel()
	throws Exception
	{
		assertEquals(0, handler1.getCallCount());
		assertEquals(0, handler2.getCallCount());
		assertEquals(0, handler3.getCallCount());
		channel.publish(new NormalEvent());
		channel.publish(new NormalEvent());
		channel.publish(new NormalEvent());
		channel.publish(new NormalEvent());
		channel.publish(new NormalEvent());
		Thread.sleep(PAUSE_MILLIS);
		assertEquals(5, handler1.getCallCount());
		assertEquals(5, handler2.getCallCount());
		assertEquals(5, handler3.getCallCount());
	}

	
	// SECTION: INNER CLASSES

	private class NormalEvent
	{
		public void kerBlooey()
		{
			// do nothing.
		}

		public String toString()
		{
			return this.getClass().getSimpleName();
		}
	}
	
	private class ErroredEvent
	extends NormalEvent
	{
		private int occurrences = 0;

		@Override
		public void kerBlooey()
		{
			if (occurrences++ < 5)
			{
				throw new RuntimeException("KER-BLOOEY!");
			}
		}
	}

	private static class TestEventHandler
	implements EventHandler
	{
		private int callCount = 0;

		@Override
		public void handle(Object event)
		{
			++callCount;
			((NormalEvent) event).kerBlooey();
		}
		
		public int getCallCount()
		{
			return callCount;
		}
	}

	private static class TestLongEventHandler
	implements EventHandler
	{
		private int callCount = 0;

		@Override
		public void handle(Object event)
		{
			++callCount;
			try
            {
				// pretend the long event takes 1 second to process...
				System.out.println("Event handler " + this.getClass().getSimpleName() + " going to sleep..." + callCount);
	            Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
	            e.printStackTrace();
            }
		}
		
		public int getCallCount()
		{
			return callCount;
		}
	}
}
