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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.strategicgains.eventing.EventBusBuilder;
import com.strategicgains.eventing.EventHandler;

/**
 * Configure and build a local EventQueue that receives events only within the current JVM.
 * 
 * @author toddf
 * @since Oct 4, 2012
 */
public class LocalEventBusBuilder
implements EventBusBuilder<LocalEventBus, LocalEventBusBuilder>
{
	private static final long DEFAULT_POLL_DELAY = 0L;

	private Set<EventHandler> subscribers = new LinkedHashSet<>();
	private Set<String> publishableEventTypes = new HashSet<>();
	private boolean shouldRepublishOnError = false;
	private long pollDelay = DEFAULT_POLL_DELAY;

	public LocalEventBusBuilder()
	{
		super();
	}

	@Override
	public LocalEventBus build()
	{
		assert(!subscribers.isEmpty());

		LocalEventBus bus = new LocalEventBus(subscribers, shouldRepublishOnError, pollDelay);
		
		for (String eventType : publishableEventTypes)
		{
			bus.addPublishableEventType(eventType);
		}
		
		return bus;
	}

    public LocalEventBusBuilder shouldRepublishOnError(boolean value)
    {
    	this.shouldRepublishOnError = value;
	    return this;
    }
    
    public LocalEventBusBuilder pollDelay(long millis)
    {
    	this.pollDelay = millis;
    	return this;
    }

    @Override
    public LocalEventBusBuilder subscribe(EventHandler handler)
    {
    	if (!subscribers.contains(handler))
    	{
    		subscribers.add(handler);
    	}
    	
    	return this;
    }

    @Override
    public LocalEventBusBuilder unsubscribe(EventHandler handler)
    {
    	subscribers.remove(handler);
    	return this;
    }
    
    public LocalEventBusBuilder addPublishableEventType(String eventType)
    {
    	publishableEventTypes.add(eventType);
    	return this;
    }
}
