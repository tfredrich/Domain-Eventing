package com.strategicgains.eventing;

/**
 * Defines an interface for typed Events.
 * 
 * @author tfredrich
 * @since 20 May 2016
 *
 */
public interface Event
{
	/**
	 * Provide the type of the event.
	 * 
	 * It is possible to use class names (simple or fully-qualified) as event
	 * types, however, when using non-local event transports such as Kafka or
	 * RabbitMQ, these event types potentially become topics to which
	 * subscriptions are registered. In this case the event types must match
	 * topic names in their respective transports.
	 * 
	 * @return a string defining the event type
	 */
	public String getType();
}
