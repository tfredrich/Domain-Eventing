package com.strategicgains.eventing;

public interface EventPredicate
{
	boolean evaluate(Object event);
}
