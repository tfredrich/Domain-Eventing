package com.strategicgains.eventing;

public class BaseSubscription
implements Subscription
{
	private Consumer consumer;

	public BaseSubscription(Consumer consumer)
	{
		super();
		this.consumer = consumer;
	}

	@Override
	public Consumer getConsumer()
	{
		return consumer;
	}
}
