Overview
========
Domain-Eventing implements the Domain Events concept from Eric Evans' Domain Driven Design.

This simple Java library provides a Singleton interface (see DomainEvents class) to an EventMonitor thread and a simple
way to raise events (which are just POJOs) throughout the domain layer.

Event handlers simply implement the EventHandler interface, which has two methods, handle(Object) and handles(Class).
The handle() method is the implementation for dealing with the domain event and handles() returns a boolean indicating
whether that particular EventHandler can process the given class.

Event Flow
==========
### Domain Model
<table border="0">
	<tr>
		<td>DomainEvents.raise(event)</td>
		<td>--------&gt;</td>
		<td>eventMonitor.raise(event)</td>
		<td>--------&gt;</td>
		<td>concurrentQueue.add(event)<br/>monitorThread.notify()</td>
	</tr>
</table>
### Monitor Thread (on notify)
<table border="0">
	<tr>
		<td>event = concurrentQueue.poll()</td>
		<td>--------&gt;</td>
		<td>Get handlers that can process the given event.<br/>Utilizes handler.handles(event).<br/>This collection of handlers<br/>is cached for later use.</td>
		<td>--------&gt;</td>
		<td>handler.handle(event)<br/>(for each handler)</td>
	</tr>
</table>

Usage
=====
1. Implement *EventHandler* interface in class(es) to process appropriate events.
   1. Implement *handles(Class)* method to return true for each DomainEvent type that the handler can process.
   2. Implement *handle(Object)* to actually process the event.
2. Call *DomainEvents.register(EventHandler)* for each EventHandler implementation.
3. Optionally, call *DomainEvents.setEventMonitorCount(n)*, where n > 1 and indicates the number of EventMonitor threads to run--to handle multiple events in parallel.
4. Optionally, call *DomainEvents.setReRaiseOnError(true)* to re-raise an event when an error occurs processing an event. 
5. Call *DomainEvents.startMonitoring()* at the beginning of your application.
6. Call *DomainEvents.raise(Object)* in your domain code where events need to be raised.
  - repeat as necessary.
7. Call *DomainEvents.stopMonitoring()* at the end of your application.

Want to manage your own EventMonitor threads?  Cool!  Then ignore the static foreign methods in the *DomainEvents* class and utilize the *EventQueue* class and *EventMonitor* thread alone.  Create as many instances of *EventMonitor* as you need, passing in the single *EventQueue* instance to each constructor. Here's the way to use *EventMonitor* on its own:

1. eventQueue = new EventQueue();
2. monitor = new EventMonitor(eventQueue);  // repeat this step for each monitor thread you need.
3. monitor.register(EventHandler) for each EventHandler implementation.
4. monitor.start(); // for each event monitor thread created in step 2.
5. monitor.setReRaiseOnError(true)--optional.
6. monitor.raise(Object) in your domain logic.
   - repeat as necessary
7. monitor.shutdown(); //for each event monitor thread created in step 2.

BTW, the above process is the same the DomainEvents manages for you.

Release Notes
=============
### 0.2.0 - under development
* Removed constraint of having to implement DomainEvent marker interface in event messages.
* Introduced EventQueue, allowing multiple EventMonitor threads to be processing events from the queue simultaneously.

### 0.1.0
* Initial release.
