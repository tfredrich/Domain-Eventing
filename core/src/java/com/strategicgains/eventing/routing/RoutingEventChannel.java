package com.strategicgains.eventing.routing;

import java.util.LinkedHashSet;
import java.util.Set;

import com.strategicgains.eventing.EventChannel;

/**
 * Selectively publishes to {@link EventChannel}s based on {@link RoutingRule}s. If the
 * rule associated with a channel applies, the message is published to that channel.
 * 
 * @author tfredrich
 * @since 3 Jun 2016
 */
public class RoutingEventChannel
implements EventChannel
{
	private Set<EventChannelRoute> channels = new LinkedHashSet<>();
	
	public boolean addChannel(RoutingRule rule, EventChannel channel)
	{
		return channels.add(new EventChannelRoute(rule, channel));
	}

	@Override
	public boolean publish(Object event)
	{
		boolean isPublished = false;

		for(EventChannelRoute route : channels)
		{
			if (route.appliesTo(event))
			{
				isPublished |= route.publish(event);
			}
		}

		return isPublished;
	}

	@Override
	public void shutdown()
	{

		for(EventChannelRoute route : channels)
		{
			route.shutdown();
		}
	}

	private class EventChannelRoute
	{
		public RoutingRule rule;
		public EventChannel channel;

		public EventChannelRoute(RoutingRule rule, EventChannel channel)
		{
			this.rule = rule;
			this.channel = channel;
		}

		public boolean appliesTo(Object event)
		{
			return rule.appliesTo(event);
		}

		public boolean publish(Object event)
		{
			return channel.publish(event);
		}

		public void shutdown()
		{
			channel.shutdown();
		}
	}
}
