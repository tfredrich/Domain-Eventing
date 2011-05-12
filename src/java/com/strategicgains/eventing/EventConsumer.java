/*
 * Copyright 2011, Strategic Gains, Inc.  All rights reserved.
 */
package com.strategicgains.eventing;

import com.strategicgains.eventing.domain.DomainEvent;


/**
 * @author toddf
 * @since May 12, 2011
 */
public interface EventConsumer
{
	public void receive(DomainEvent event);
	public boolean handles(Class<? extends DomainEvent> eventClass);
}
