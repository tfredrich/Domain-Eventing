/*
 * Copyright 2011, Pearson eCollege.  All rights reserved.
 */
package com.strategicgains.eventing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
	
	@BeforeClass
	public static void startup()
	{
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
	}

	@Test
	public void shouldNotifyEventHandlerMultipleTimes()
	throws Exception
	{
		assertEquals(0, handler.getCallCount());
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
	}

	@Test
	public void shouldNotNotifyEventHandler()
	throws Exception
	{
		assertEquals(0, handler.getCallCount());
		DomainEvents.raise(new IgnoredEvent());
		Thread.sleep(5);
		assertEquals(0, handler.getCallCount());
		assertEquals(1, ignoredHandler.getCallCount());
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

	private class DomainEventsTestHandler
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

	private class DomainEventsTestIgnoredEventsHandler
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
}
