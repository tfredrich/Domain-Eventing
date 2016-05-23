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

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.strategicgains.eventing.Consumer;
import com.strategicgains.eventing.Subscription;
import com.strategicgains.eventing.Transport;

/**
 * @author toddf
 * @since Oct 18, 2012
 */
public class LocalEventTransport
implements Transport
{
	private Queue<Object> queue = new ConcurrentLinkedQueue<>();
	private EventMonitor monitor;

	public LocalEventTransport(Collection<Consumer> handlers, boolean shouldReraiseOnError, long pollDelayMillis)
	{
		super();
		initializeMonitor(handlers, shouldReraiseOnError, pollDelayMillis);
	}

	/**
	 * @param handlers
	 */
	private void initializeMonitor(Collection<Consumer> handlers, boolean shouldReraiseOnError, long pollDelayMillis)
	{
		monitor = new EventMonitor(this, pollDelayMillis);

		for (Consumer handler : handlers)
		{
			monitor.register(handler);
		}

		retryOnError(shouldReraiseOnError);
		monitor.start();
	}

	public boolean isEmpty()
	{
		return queue.isEmpty();
	}

	public Object poll()
	{
		return queue.poll();
	}

	@Override
	public void publish(Object event)
	{
		queue.add(event);

		synchronized (this)
		{
			notifyAll();
		}
	}

	/**
	 * @param value
	 */
    public void retryOnError(boolean value)
    {
		monitor.setReRaiseOnError(value);
    }

	@Override
	public void shutdown()
	{
		queue.clear();
		queue = null;
	}

    @Override
    public Subscription subscribe(Consumer handler)
    {
		return monitor.register(handler);
    }

    @Override
    public void unsubscribe(Subscription subscription)
    {
    	monitor.unregister(subscription.getConsumer());
    }
}
