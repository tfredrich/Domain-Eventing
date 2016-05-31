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

import java.util.LinkedHashSet;
import java.util.Set;

import com.strategicgains.eventing.Consumer;
import com.strategicgains.eventing.Producer;
import com.strategicgains.eventing.TransportBuilder;

/**
 * Configure and build a local EventQueue that receives events only within the current JVM.
 * 
 * @author toddf
 * @since Oct 4, 2012
 */
public class LocalTransportBuilder
implements TransportBuilder<LocalTransport, LocalTransportBuilder>
{
	private static final long DEFAULT_POLL_DELAY = 0L;

	private Set<Producer> producers = new LinkedHashSet<>();
	private Set<Consumer> consumers = new LinkedHashSet<>();
	private boolean shouldRepublishOnError = false;
	private long pollDelay = DEFAULT_POLL_DELAY;

	public LocalTransportBuilder()
	{
		super();
	}

	@Override
	public LocalTransport build()
	{
		LocalTransport t = new LocalTransport(consumers, shouldRepublishOnError, pollDelay);

		producers.forEach(new java.util.function.Consumer<Producer>()
		{
			@Override
			public void accept(Producer producer)
			{
				t.register(producer);
			}
		});

		return t;
	}

    public LocalTransportBuilder shouldRepublishOnError(boolean value)
    {
    	this.shouldRepublishOnError = value;
	    return this;
    }
    
    public LocalTransportBuilder pollDelay(long millis)
    {
    	this.pollDelay = millis;
    	return this;
    }

    @Override
    public LocalTransportBuilder subscribe(Consumer handler)
    {
   		consumers.add(handler);
    	return this;
    }

    @Override
    public LocalTransportBuilder unsubscribe(Consumer handler)
    {
    	consumers.remove(handler);
    	return this;
    }

	@Override
	public LocalTransportBuilder register(Producer producer)
	{
		producers.add(producer);
		return this;
	}
}
