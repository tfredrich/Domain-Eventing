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

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.strategicgains.eventing.EventHandler;
import com.strategicgains.eventing.EventQueue;

/**
 * @author toddf
 * @since Feb 6, 2012
 */
public class LocalEventQueue
extends EventQueue
{
	private EventMonitor monitor;

	public LocalEventQueue(List<EventHandler> handlers, boolean shouldReraiseOnError, long pollDelayMillis)
	{
		super(new ConcurrentLinkedQueue<Object>());
		initializeMonitor(handlers, shouldReraiseOnError, pollDelayMillis);
	}

	/**
	 * @param handlers
	 */
	private void initializeMonitor(List<EventHandler> handlers, boolean shouldReraiseOnError, long pollDelayMillis)
	{
		this.monitor = new EventMonitor(this, pollDelayMillis);

		for (EventHandler handler : handlers)
		{
			this.monitor.register(handler);
		}

		this.monitor.setReRaiseOnError(shouldReraiseOnError);
		this.monitor.start();
	}

	@Override
    public void shutdown()
    {
		monitor.shutdown();
    }
}
