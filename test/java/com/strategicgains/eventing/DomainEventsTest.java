/*
 * Copyright 2011, Pearson eCollege.  All rights reserved.
 */
package com.strategicgains.eventing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.strategicgains.eventing.domain.DomainEvent;


/**
 * @author toddf
 * @since May 18, 2011
 */
public class DomainEventsTest
{
	private DomainEventsTestHandler handler = new DomainEventsTestHandler();
	private DomainEventsTestIgnoredEventsHandler ignoredHandler = new DomainEventsTestIgnoredEventsHandler();
	private DomainEventsTestLongEventHandler longHandler = new DomainEventsTestLongEventHandler();

	@BeforeClass
	public static void startup()
	{
		DomainEvents.setEventMonitorCount(5);
		DomainEvents.startMonitoring();
	}
	
	@AfterClass
	public static void shutdown()
	{
		DomainEvents.stopMonitoring();
	}

	@Before
	public void setup()
	{
		DomainEvents.register(handler);
		DomainEvents.register(ignoredHandler);
		DomainEvents.register(longHandler);
	}
	
	@After
	public void teardown()
	{
		DomainEvents.unregister(handler);
		DomainEvents.unregister(ignoredHandler);
		DomainEvents.unregister(longHandler);
	}

	@Test
	public void isSingleton()
	{
		assertTrue(DomainEvents.instance() == DomainEvents.instance());
	}

	@Test
	public void shouldNotifyEventHandler()
	throws Exception
	{
		assertEquals(0, handler.getCallCount());
		DomainEvents.raise(new HandledEvent());
		Thread.sleep(5);
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
		DomainEvents.raise(new HandledEvent());
		DomainEvents.raise(new IgnoredEvent());
		DomainEvents.raise(new HandledEvent());
		DomainEvents.raise(new IgnoredEvent());
		DomainEvents.raise(new HandledEvent());
		DomainEvents.raise(new IgnoredEvent());
		DomainEvents.raise(new HandledEvent());
		DomainEvents.raise(new IgnoredEvent());
		DomainEvents.raise(new HandledEvent());
		DomainEvents.raise(new IgnoredEvent());
		Thread.sleep(5);
		assertEquals(5, handler.getCallCount());
		assertEquals(5, ignoredHandler.getCallCount());
		assertEquals(0, longHandler.getCallCount());
	}

	@Test
	public void shouldNotNotifyEventHandler()
	throws Exception
	{
		assertEquals(0, ignoredHandler.getCallCount());
		DomainEvents.raise(new IgnoredEvent());
		Thread.sleep(5);
		assertEquals(0, handler.getCallCount());
		assertEquals(1, ignoredHandler.getCallCount());
		assertEquals(0, longHandler.getCallCount());
	}

	@Test
	public void shouldRetryEventHandler()
	throws Exception
	{
		assertEquals(0, handler.getCallCount());
		DomainEvents.raise(new ErroredEvent());
		Thread.sleep(50);
		assertEquals(6, handler.getCallCount());
		assertEquals(0, ignoredHandler.getCallCount());
		assertEquals(0, longHandler.getCallCount());
	}

	@Test
	public void shouldNotRetryEventHandler()
	throws Exception
	{
		DomainEvents.setReRaiseOnError(false);
		assertEquals(0, handler.getCallCount());
		DomainEvents.raise(new ErroredEvent());
		Thread.sleep(50);
		assertEquals(1, handler.getCallCount());
		assertEquals(0, ignoredHandler.getCallCount());
		assertEquals(0, longHandler.getCallCount());
	}

	@Test
	public void shouldProcessInParallel()
	throws Exception
	{
		DomainEvents.setReRaiseOnError(false);
		assertEquals(0, longHandler.getCallCount());
		DomainEvents.raise(new LongEvent());
		DomainEvents.raise(new LongEvent());
		DomainEvents.raise(new LongEvent());
		DomainEvents.raise(new LongEvent());
		DomainEvents.raise(new LongEvent());
		Thread.sleep(100);
		assertEquals(0, handler.getCallCount());
		assertEquals(0, ignoredHandler.getCallCount());
		System.out.println("longHandler instance=" + longHandler.toString());
		assertEquals(5, longHandler.getCallCount());
	}

	
	// SECTION: INNER CLASSES

	private class HandledEvent
	implements DomainEvent
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
	implements DomainEvent
	{
	}
	
	private class LongEvent
	implements DomainEvent
	{
	}

	private static class DomainEventsTestHandler
	implements EventHandler
	{
		private int callCount = 0;

		@Override
		public void handle(DomainEvent event)
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
		public boolean handles(Class<? extends DomainEvent> eventClass)
		{
			if (HandledEvent.class.isAssignableFrom(eventClass))
			{
				return true;
			}
			
			return false;
		}		
	}

	private static class DomainEventsTestIgnoredEventsHandler
	implements EventHandler
	{
		private int callCount = 0;

		@Override
		public void handle(DomainEvent event)
		{
			assert(event.getClass().equals(IgnoredEvent.class));
			++callCount;
		}
		
		public int getCallCount()
		{
			return callCount;
		}

		@Override
		public boolean handles(Class<? extends DomainEvent> eventClass)
		{
			if (IgnoredEvent.class.isAssignableFrom(eventClass))
			{
				return true;
			}
			
			return false;
		}		
	}

	private static class DomainEventsTestLongEventHandler
	implements EventHandler
	{
		private int callCount = 0;

		@Override
		public void handle(DomainEvent event)
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
		public boolean handles(Class<? extends DomainEvent> eventClass)
		{
			if (LongEvent.class.isAssignableFrom(eventClass))
			{
				return true;
			}
			
			return false;
		}		
	}
}
