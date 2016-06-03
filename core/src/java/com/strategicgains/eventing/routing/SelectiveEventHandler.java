package com.strategicgains.eventing.routing;

import com.strategicgains.eventing.EventHandler;

public interface SelectiveEventHandler
extends EventHandler
{
	boolean isSelected(Object event);
}
