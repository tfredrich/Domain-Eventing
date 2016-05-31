package com.strategicgains.eventing;

public class BaseRegistration
implements Registration
{
	private Producer producer;

	public BaseRegistration(Producer producer)
	{
		super();
		this.producer = producer;
	}

	@Override
	public Producer getProducer()
	{
		return producer;
	}
}
