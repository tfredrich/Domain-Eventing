package com.strategicgains.eventing;

public class AbstractEvent
implements Event
{
	@Override
	public String getEventType()
	{
		return getClass().getName();
	}
}
