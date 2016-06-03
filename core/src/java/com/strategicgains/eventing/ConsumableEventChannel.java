package com.strategicgains.eventing;

/**
 * An {@link EventChannel} that can be consumed in bulk, in addition to being subscribed-to.
 * 
 * @author tfredrich
 * @since 2 Jun 2016
 */
public interface ConsumableEventChannel
extends Consumable, EventChannel
{
}
