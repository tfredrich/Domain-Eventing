Kafka-based Domain Eventing
===========================

Apache Kafka (kafka.apache.org) is publish-subscribe messaging rethought as a distributed commit log.
This library makes it straigh-forward to use Kafka as a simple domain eventing bus.

Configuring an Event Bus
=========================

We need an EventBus implementation to pass messages on. We'll use Kafka to pass messages. This only needs to happen at application startup.

Using a Builder
---------------

```java
// Allocation one or more EventHandler implementations.
DomainEventsTestHandler handler = new DomainEventsTestHandler();

EventBus kafkaBus = new KafkaEventBusBuilder()
	.actorSystem(actorSystem)					// Optional.
	.subscribe(handler)							// Subscribe your EventHandler implementation(s).
    .build();									// Build the EventBus.

kafkaBus.addPublishableEventType(Message.class);	// Optional. Denote the event Class(es) that this bus can publish.
```

Using Constructors
------------------

```java
EventBus kafkaBus = new KafkaEventBus();			// Uses the default ActorSystem, named 'AkkaDomainEventing'
// Or...
EventBus kafkaBus = new KafkaEventBus();	//

kafkaBus.addPublishableEventType(Message.class);	// Optional. Denote the event Class(es) that this bus can publish.

// Allocation one or more EventHandler implementations.
DomainEventsTestHandler handler = new DomainEventsTestHandler();
kafkaBus.subscribe(handler);						// Subscribe your EventHandler implementation(s).
```

Configuring DomainEvents
========================

Once the EventBus is configured, you can either used it directly to publish events, or you can add it to the Singleton DomainEvents object to allow publishing from anywhere in your application. Since DomainEvents allows multiple EventBus instances to be added, you can publish events to multiple buses at once. Buses are added by name, where the name
for each bus must be unique.

For example, you may want a 'local' bus that is configured to send certain events only within the JVM and a 'remote' bus that sends events to JVMs in a cluster. DomainEvents will determine which events to send to which bus using the addPublishableEventType() method on each EventBus. Otherwise, it sends all events to all buses.

```java
DomainEvents.addEventBus('kafka', kafkaBus);
```

That's all there is to it. Now we're ready to publish events.

Publishing Events
=================

Send your event POJOs to the event buses configured in DomainEvents.

```java
// Define arbitrary POJOs to describe your event(s)
public class Event
{
	private String data;

	public Event(String data)
	{
		this.data = data;
	}

	public String getData()
	{
		return data;
	}
}

...


// Publish a new event...
DomainEvents.publish(new Event("something happened!"));
```

That's all. Events are just plain-old Java objects. DomainEvents will take care of delivering your message to the appropriate, configured EventBus instances.

Handling Events
===============

So far we've only published events. To consume events on the event bus, we have to build an EventHandler implementation and subscribe it to the event bus. Subscribe
only once at application startup as shown in the above section, *Configuring a Message Bus*.

An EventHandler is very simple. It only has two methods to implement:

* boolean handles(Class<?>) - which allows you to tell Domain-Eventing which event types the EventHandler cares about. Just return true for every event object Class that the handler can process.
* void handle(Object) - which is the business logic to process the message.

Here, we'll create an example for the above event bus configuration to handle Event POJOs described above, simply printing out the string 'data' property on the event.

```java
public class DomainEventsTestHandler
implements EventHandler
{
	/**
	 * Process the incoming message, which will be of type Event, because
	 * Domain-Events will only send types based on the results of the
	 * handles(Class) method below.
	 */
	@Override
	public void handle(Object message)
	{
		Event event = (Event) message;

		System.out.println(event.getData());
	}

	/**
	 * We want to process messages of type Event in this handler.
	 * So we'll return true if the parameter, eventClass, is of type Event.
	 */
	@Override
	public boolean handles(Class<?> eventClass)
	{
		if (Event.class.isAssignableFrom(eventClass))
		{
			return true;
		}

		return false;
	}		
}
```

Cleaning Up
===========

At application shutdown, we need to cleanup the resource being held by the EventBus implementation(s) and allow them to finish processing messages or deallocating resources
as necessary.

If you are using the EventBus directly, simply call shutdown() on the bus instance as follows:

```java
	kafkaBus.shutdown();
```

Or, if you're using the Singleton, DomainEvents (recommended), call:

```java
	DomainEvents.shutdown();
```

Which shuts down all the event buses registered with the DomainEvents singleton class.