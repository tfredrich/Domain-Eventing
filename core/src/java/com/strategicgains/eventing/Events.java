package com.strategicgains.eventing;

/**
 * Foreign static helper methods for event objects.
 * 
 * @author tfredrich
 * @since 20 May 2016
 */
public class Events
{
	/**
	 * Returns the event type of a given event. If the event implements {@link Event}, then 
	 * getEventType() is called. Otherwise, the fully-qualified classname is returned.
	 * 
	 * @param event
	 * @return
	 */
	public static final String getEventType(Object event)
	{
		if (Event.class.isAssignableFrom(event.getClass()))
		{
			return ((Event)event).getEventType();
		}

		return getClassEventType(event);
	}

	/**
	 * Simply returns the fully-qualified classname of the object.
	 * 
	 * @param pojo
	 * @return
	 */
	public static final String getClassEventType(Object pojo)
	{
		return pojo.getClass().getName();
	}
}
