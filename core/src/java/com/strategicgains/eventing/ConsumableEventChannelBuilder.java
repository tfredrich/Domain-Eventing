package com.strategicgains.eventing;

/**
 * Allow (un)registration of {@link EventConsumer}s with the {@link EventChannel}.
 * 
 * @author tfredrich
 *
 * @param <T> The type of the underlying EventChannel
 * @param <B> The type of this builder.
 */
public interface ConsumableEventChannelBuilder<T  extends ConsumableEventChannel, B extends ConsumableEventChannelBuilder<?, ?>>
extends EventChannelBuilder<T, B>
{
	public B register(EventConsumer consumer);
	public B unregister(EventConsumer consumer);
}
