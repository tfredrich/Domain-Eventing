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

import com.strategicgains.eventing.EventBus;
import com.strategicgains.eventing.EventHandler;

/**
 * @author toddf
 * @since Feb 6, 2012
 */
public class LocalEventBus
extends EventBus
{
	public LocalEventBus(Collection<EventHandler> handlers, boolean shouldReraiseOnError, long pollDelayMillis)
	{
		super(new LocalEventTransport(handlers, shouldReraiseOnError, pollDelayMillis));
	}

	/**
	 * @param value
	 */
    public void retryOnError(boolean value)
    {
    	((LocalEventTransport) getTransport()).retryOnError(value);
    }
}
