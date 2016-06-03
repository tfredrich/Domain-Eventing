package com.strategicgains.eventing;

public interface Consumable
{
	/**
	 * Get an object from the channel, blocking until data is available.
	 * 
	 * @return
	 */
	Object get();

	/**
	 * Get an object from the channel, blocking at most timeout number of milliseconds.
	 * 
	 * @param timeoutMillis maximum number of milliseconds to wait for data.
	 * @return an event object or null if none available.
	 */
	Object get(long timeoutMillis);
}
