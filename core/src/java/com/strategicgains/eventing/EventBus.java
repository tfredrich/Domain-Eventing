package com.strategicgains.eventing;

public interface EventBus
extends Publisher
{

	/**
	 * Publish the event, if this event bus can publish events of that type.
	 * 
	 * @param event an event to publish.
	 */
	void publish(Object event);

	/**
	 * Create a subscription to this event bus, providing an {@link Consumer} implementation to process events.
	 * 
	 * @param consumer a {@link Consumer} implementation to process events.
	 * @return a {@link Subscription} instance if successful.
	 */
	Subscription subscribe(Consumer consumer);

	/**
	 * Removes the subscription to this event bus.
	 * 
	 * @param subscription a {@link Subscription} instance returned from subscribe()
	 */
	void unsubscribe(Subscription subscription);

	/**
	 * Shutdown this event bus and release its resources.
	 */
	void shutdown();

}