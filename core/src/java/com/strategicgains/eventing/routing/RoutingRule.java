package com.strategicgains.eventing.routing;

public interface RoutingRule
{
	boolean appliesTo(Object event);
}
