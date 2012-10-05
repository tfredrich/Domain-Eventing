/*
    Copyright 2011, Strategic Gains, Inc.

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
package com.strategicgains.eventing;

import java.util.ArrayList;
import java.util.List;


/**
 * DomainEvents defines a static public interface for raising and handling domain events.
 * Raising an event places it in an in-memory queue that is then handled asynchronously
 * by an EventMonitor.
 * <p/>
 * Domain events are publish-subscribe, where when an event is raised, all appropriate
 * EventHandler instances are notified of that event.
 * <p/>
 * To utilize domain events in your applications:
 * <ol>
 * <li>Implement DomainEvent sub-class(es) to represent each event type.</li>
 * <li>Implement EventHandler sub-class(es) for processing the events, overriding the handles(Class)
 * method to describe which DomainEvent sub-types the handler can process.</li>
 * <li>DomainEvents.register(new <EventHandlerSubType>()); // In main() or startup for each EventHandler.</li>
 * <li>(optional) DomainEvents.useDistributedEvents(); // Before startMonitoring() called to support eventing within a cluster.
 * <li>DomainEvents.startMonitoring(); // In main() when ready to process events.
 * <li>Call DomainEvents.raise(new DomainEventSubType()) wherever domain events should be raised.
 * <li>DomainEvents.stopMonitoring(); // On application shutdown (e.g. in main()).
 * </ol>
 * 
 * All raised DomainEvent instances are handled asynchronously, in a separate Thread, which is
 * the EventMonitor managed by DomainEvents.
 * 
 * @author toddf
 * @since May 12, 2011
 */
public class DomainEvents
{
	// SECTION: CONSTANTS

	private static final DomainEvents INSTANCE = new DomainEvents();

	
	// SECTION: INSTANCE VARIABLES

	private List<EventQueue> eventQueues = new ArrayList<EventQueue>();


	// SECTION: CONSTRUCTOR

	private DomainEvents()
	{
		super();
	}


	// SECTION: STATIC METHODS

	/**
	 * Get the Singleton instance of DomainEvents.
	 */
	public static DomainEvents instance()
	{
		return INSTANCE;
	}

	/**
	 * Raise an event, passing it to applicable consumers synchronously.  This method is
	 * equivalent to calling instance().raiseEvent(DomainEvent).
	 * 
	 * @param event the DomainEvent to raise.
	 */
	public static void raise(Object event)
	{
		instance().raiseEvent(event);
	}

	public static boolean addQueue(EventQueue queue)
	{
		return instance().addEventQueue(queue);
	}
	
	public static void shutdown()
	{
		instance().shutdownEventQueues();
	}


	// SECTION: INSTANCE METHODS

	private boolean addEventQueue(EventQueue queue)
	{
		if (!eventQueues.contains(queue))
		{
			eventQueues.add(queue);
			return true;
		}
		
		return false;
	}

	/**
	 * Raise an event, passing it to applicable consumers asynchronously.
	 * 
	 * @param event
	 */
	private void raiseEvent(Object event)
	{
		for (EventQueue eventQueue : eventQueues)
		{
			eventQueue.raise(event);
		}
	}

	private void shutdownEventQueues()
	{
		for (EventQueue eventQueue : eventQueues)
		{
			eventQueue.shutdown();
		}
	}
}
