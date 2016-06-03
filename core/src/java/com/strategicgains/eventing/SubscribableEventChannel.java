package com.strategicgains.eventing;

public interface SubscribableEventChannel
extends EventChannel
{
	/**
	 * Subscribe a handler to this event channel for the given eventTypes.
	 * 
	 * @param handler an event handler that implements the {@link EventHandler} interface.
	 * @return true if the subscription was successful. Otherwise, false.
	 */
	public boolean subscribe(EventHandler handler);

	/**
	 * Remove a subscription from the underlying event channel.
	 * 
	 * @param handler an {@link EventHandler}
	 */
	public void unsubscribe(EventHandler handler);
}
