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

import java.util.ArrayList;
import java.util.List;

import com.strategicgains.eventing.EventHandler;
import com.strategicgains.eventing.EventBusBuilder;

/**
 * Configure and build a local EventQueue that receives events only within the current JVM.
 * 
 * @author toddf
 * @since Oct 4, 2012
 */
public class LocalEventBusBuilder<T>
implements EventBusBuilder<LocalEventBus<T>, LocalEventBusBuilder<T>>
{
	private static final long DEFAULT_POLL_DELAY = 0L;

	private List<EventHandler> subscribers = new ArrayList<EventHandler>();
	private boolean shouldReraiseOnError = false;
	private long pollDelay = DEFAULT_POLL_DELAY;

	public LocalEventBusBuilder()
	{
		super();
	}

	@Override
	public LocalEventBus<T> build()
	{
		assert(!subscribers.isEmpty());

		return new LocalEventBus<T>(subscribers, shouldReraiseOnError, pollDelay);
	}

    public LocalEventBusBuilder<T> shouldReraiseOnError(boolean value)
    {
    	this.shouldReraiseOnError = value;
	    return this;
    }
    
    public LocalEventBusBuilder<T> pollDelay(long millis)
    {
    	this.pollDelay = millis;
    	return this;
    }

    @Override
    public LocalEventBusBuilder<T> subscribe(EventHandler handler)
    {
    	if (!subscribers.contains(handler))
    	{
    		subscribers.add(handler);
    	}
    	
    	return this;
    }
}
