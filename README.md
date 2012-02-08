Overview
========
Domain-Eventing implements the Domain Events concept from Eric Evans' Domain Driven Design.

This simple Java library provides a Singleton interface (see DomainEvents class) to one or more EventMonitor threads and a simple
way to raise events (which implement the DomainEvent marker interface) throughout the domain layer.

Event handlers simply implement the EventHandler interface, which has two methods, handle(DomainEvent) and handles(Class).
The handle() method is the implementation for dealing with the domain event and handles() returns a boolean indicating
whether that particular EventHandler can process the given DomainEvent class.

Event Flow
==========
### Domain Model
<table border="0">
	<tr>
		<td>DomainEvents.raise(event)</td>
		<td>----------&gt;</td>
		<td>eventMonitor.raise(event)</td>
		<td>----------&gt;</td>
		<td>concurrentQueue.add(event)<br/>monitorThread.notify()</td>
	</tr>
</table>
### Monitor Thread (on notify)
<table border="0">
	<tr>
		<td>event = concurrentQueue.poll()</td>
		<td>----------&gt;</td>
		<td>Get handlers that can process the given event.<br/>Utilizes handler.handles(event).<br/>This collection of handlers<br/>is cached for later use.</td>
		<td>----------&gt;</td>
		<td>handler.handle(event)<br/>(for each handler)</td>
	</tr>
</table>

Usage
=====
1. Implement *DomainEvent* marker interface in the desired event types.
2. Implement *EventHandler* interface in class(es) to process appropriate events.
   1. Implement *handles(Class)* method to return true for each DomainEvent type that the handler can process.
   2. Implement *handle(DomainEvent)* to actually process the event.
3. Call *DomainEvents.register(EventHandler)* for each EventHandler implementation.
4. Optionally, call *DomainEvents.setEventMonitorCount(n)*, where n > 1 and indicates the number of EventMonitor threads to run--to handle multipe events in parallel.
5. Optionally, call *DomainEvents.setReRaiseOnError(true)* to re-raise an event when an error occurs processing an event. 
6. Call *DomainEvents.startMonitoring()* at the beginning of your application.
7. Call *DomainEvents.raise(DomainEvent)* in your domain code where events need to be raised.
   - repeat as necessary.
8. Call *DomainEvents.stopMonitoring()* at the end of your application.

Want to manage your own EventMonitor threads?  Cool!  Then ignore the static foreign methods in the *DomainEvents* class and utilize the *EventQueue* class and *EventMonitor* thread alone.  Create as many instances of *EventMonitor* as you need.  Aside from the creation of *DomainEvent* and *EventHandler* implementations (those parts are the same, see steps #1 & #2 above), here's the way to use *EventMonitor* on its own:

1. monitor = new EventMonitor()
2. monitor.register(EventHandler) for each EventHandler implementation.
3. monitor.start()
4. monitor.setReRaiseOnError(true)--optional.
5. monitor.raise(DomainEvent) in your domain logic.
   - repeat as necessary
6. monitor.shutdown() at the end of your application, for each individual EventMonitor instance.
