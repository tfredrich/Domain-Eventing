package com.strategicgains.eventing.exception;

public class EventingException
extends RuntimeException
{
	private static final long serialVersionUID = 97644221728256045L;

	public EventingException()
	{
		super();
	}

	public EventingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EventingException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public EventingException(String message)
	{
		super(message);
	}

	public EventingException(Throwable cause)
	{
		super(cause);
	}
}
