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

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.strategicgains.eventing.EventHandler;

/**
 * @author toddf
 * @since Oct 5, 2012
 */
public class LocalEventBusTest
{
	private static final int PAUSE_MILLIS = 300;
	private DomainEventsTestHandler handler = new DomainEventsTestHandler();
	private DomainEventsTestIgnoredEventsHandler ignoredHandler = new DomainEventsTestIgnoredEventsHandler();
	private DomainEventsTestLongEventHandler longHandler = new DomainEventsTestLongEventHandler();
	private LocalEventBus queue;

	@Before
	public void setup()
	{
		queue = new LocalEventBus(Arrays.asList(handler, ignoredHandler, longHandler), false, 0L);
	}
	
	@After
	public void teardown()
	{
		queue.shutdown();
	}

	@Test
	public void shouldNotifyEventHandler()
	throws Exception
	{
		assertEquals(0, handler.getCallCount());
		queue.publish(new HandledEvent());
		Thread.sleep(PAUSE_MILLIS);
		assertEquals(1, handler.getCallCount());
		assertEquals(0, ignoredHandler.getCallCount());
		assertEquals(0, longHandler.getCallCount());
	}

	@Test
	public void shouldNotifyEventHandlerMultipleTimes()
	throws Exception
	{
		assertEquals(0, handler.getCallCount());
		assertEquals(0, ignoredHandler.getCallCount());
		queue.publish(new HandledEvent());
		queue.publish(new IgnoredEvent());
		queue.publish(new HandledEvent());
		queue.publish(new IgnoredEvent());
		queue.publish(new HandledEvent());
		queue.publish(new IgnoredEvent());
		queue.publish(new HandledEvent());
		queue.publish(new IgnoredEvent());
		queue.publish(new HandledEvent());
		queue.publish(new IgnoredEvent());
		Thread.sleep(PAUSE_MILLIS);
		assertEquals(5, handler.getCallCount());
		assertEquals(5, ignoredHandler.getCallCount());
		assertEquals(0, longHandler.getCallCount());
	}

	@Test
	public void shouldNotNotifyEventHandler()
	throws Exception
	{
		assertEquals(0, ignoredHandler.getCallCount());
		queue.publish(new IgnoredEvent());
		Thread.sleep(PAUSE_MILLIS);
		assertEquals(0, handler.getCallCount());
		assertEquals(1, ignoredHandler.getCallCount());
		assertEquals(0, longHandler.getCallCount());
	}

	@Test
	public void shouldRetryEventHandler()
	throws Exception
	{
		queue.retryOnError(true);

		assertEquals(0, handler.getCallCount());
		queue.publish(new ErroredEvent());
		Thread.sleep(PAUSE_MILLIS);
		assertEquals(6, handler.getCallCount());
		assertEquals(0, ignoredHandler.getCallCount());
		assertEquals(0, longHandler.getCallCount());
	}

	@Test
	public void shouldNotRetryEventHandler()
	throws Exception
	{
		assertEquals(0, handler.getCallCount());
		queue.publish(new ErroredEvent());
		Thread.sleep(PAUSE_MILLIS);
		assertEquals(1, handler.getCallCount());
		assertEquals(0, ignoredHandler.getCallCount());
		assertEquals(0, longHandler.getCallCount());
	}

	@Test
	public void shouldProcessInParallel()
	throws Exception
	{
		assertEquals(0, longHandler.getCallCount());
		queue.publish(new LongEvent());
		queue.publish(new LongEvent());
		queue.publish(new LongEvent());
		queue.publish(new LongEvent());
		queue.publish(new LongEvent());
		Thread.sleep(PAUSE_MILLIS);
		assertEquals(0, handler.getCallCount());
		assertEquals(0, ignoredHandler.getCallCount());
		assertEquals(5, longHandler.getCallCount());
	}

	@Test
	public void shouldOnlyPublishSelected()
	throws Exception
	{
		queue.addPublishableEventType(HandledEvent.class.getName());

		assertEquals(0, handler.getCallCount());
		assertEquals(0, ignoredHandler.getCallCount());
		queue.publish(new HandledEvent());
		queue.publish(new IgnoredEvent());
		queue.publish(new HandledEvent());
		queue.publish(new IgnoredEvent());
		queue.publish(new HandledEvent());
		queue.publish(new IgnoredEvent());
		queue.publish(new HandledEvent());
		queue.publish(new IgnoredEvent());
		queue.publish(new HandledEvent());
		queue.publish(new IgnoredEvent());
		Thread.sleep(PAUSE_MILLIS);
		assertEquals(5, handler.getCallCount());
		assertEquals(0, ignoredHandler.getCallCount());
		assertEquals(0, longHandler.getCallCount());
	}

	
	// SECTION: INNER CLASSES

	private class HandledEvent
	{
		public void kerBlooey()
		{
			// do nothing.
		}
	}
	
	private class ErroredEvent
	extends HandledEvent
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
	
	private class IgnoredEvent
	{
	}
	
	private class LongEvent
	{
	}

	private static class DomainEventsTestHandler
	implements EventHandler
	{
		private int callCount = 0;

		@Override
		public void handle(Object event)
		{
			assert(HandledEvent.class.isAssignableFrom(event.getClass()));

			++callCount;
			((HandledEvent) event).kerBlooey();
		}
		
		public int getCallCount()
		{
			return callCount;
		}

		@Override
		public Collection<String> getHandledEventTypes()
		{
			return Arrays.asList(HandledEvent.class.getName(), ErroredEvent.class.getName());
		}		
	}

	private static class DomainEventsTestIgnoredEventsHandler
	implements EventHandler
	{
		private int callCount = 0;

		@Override
		public void handle(Object event)
		{
			assert(event.getClass().equals(IgnoredEvent.class));
			++callCount;
		}
		
		public int getCallCount()
		{
			return callCount;
		}

		@Override
		public Collection<String> getHandledEventTypes()
		{
			return Arrays.asList(IgnoredEvent.class.getName());
		}		
	}

	private static class DomainEventsTestLongEventHandler
	implements EventHandler
	{
		private int callCount = 0;

		@Override
		public void handle(Object event)
		{
			assert(event.getClass().equals(LongEvent.class));
			++callCount;
			try
            {
				// pretend the long event takes 1 second to process...
				System.out.println("Event handler " + this.toString() + " going to sleep..." + callCount);
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

		@Override
		public Collection<String> getHandledEventTypes()
		{
			return Arrays.asList(LongEvent.class.getName());
		}		
	}
}
