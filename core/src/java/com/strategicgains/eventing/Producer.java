package com.strategicgains.eventing;

import java.util.Collection;

/**
 * Defines the interface for objects that can produce (publish) domain events
 * of particular type(s).
 * 
 * Implementations of this interface are registered with a {@link Transport} via
 * a call to register(Producer) on the event transport itself.
 * 
 * Depending on the underlying implementation of the transport system, the act
 * of registering a producer may materialize a topic (or queue) in an external
 * messaging system.
 * 
 * @author toddf
 * @since May 12, 2011
 */
public interface Producer
{
	/**
	 * Publish the given event, possibly augmenting and/or formatting the event
	 * before pushing it to the {@link Transport}
	 * 
	 * @param event an event (or message) to publish.
	 * @param the transport on which to publish the event.
	 * @throws Exception if something went wrong
	 */
	public void publish(Object event, Transport transport)
	throws Exception;

	/**
	 * Provides a list of event types that this Publisher can produce.
	 * Only messages of the given types will be sent from this producer.
	 * 
	 * It is possible to use class names (simple or fully-qualified), however,
	 * when using non-local event transports such as Kafka or RabbitMQ, these
	 * event types become topics to which subscriptions are registered. In
	 * that case the event types must match topic names in those transports.
	 * 
	 * @return a collection of strings containing event type names.
	 */
	public Collection<String> getProducedEventTypes();
}
