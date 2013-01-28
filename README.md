[![Build Status](https://buildhive.cloudbees.com/job/tfredrich/job/Domain-Eventing/badge/icon)](https://buildhive.cloudbees.com/job/tfredrich/job/Domain-Eventing/)

Overview
========
Domain-Eventing implements the Domain Events concept from Eric Evans' Domain Driven Design.  Enables simple messaging
for domain models within a single JVM, or using a distributed event bus, message across a cluster of JVMs.

This simple Java library provides a Singleton interface (see DomainEvents class) to create event bus(ses) and to
publish events (which are just POJOs) throughout the domain layer (POJOs must be Serializable for a distributed
event bus).

Event handlers simply implement the EventHandler interface, which has two methods, handle(Object) and handles(Class).
The handle() method is the implementation for processing the domain event and handles() returns a boolean indicating
whether that particular EventHandler can process the given event.

Why Domain Eventing Instead of Messaging or ESB?
================================================
Messaging systems or ESB (Enterprise Service Bus) are very heavy and resource intensive. Small, quick, inter-application
messages don't usually need to be broadcast enterprise wide.  For instance, within an eventual-consistency database
model, cascade deletes may occur asynchronously, outside of the request.  This is a great candidate for inter-application
eventing instead of leveraging full-up JMS or other messaging system.

The domain eventing model supported is publish/subscribe (pub/sub)--sending messages to all subsribers that can process it.
There is no concept within this library of point-to-point or single consumer for a message and is, therefore, left as
an exercise for the reader... :-)

Event Production
================
In this model, published events stay within the current Java virtual machine (JVM).  This is the simplest and fastest option.
However, as published events are in an in-memory queue, it is possible to lose messages if the JVM goes down unexpectedly.

Event Flow
==========
### Domain Model
<table border="0">
	<tr>
		<td>DomainEvents.raise(event)</td>
		<td>---&gt;</td>
		<td>eventMonitor.raise(event)</td>
		<td>---&gt;</td>
		<td>concurrentQueue.add(event)<br/>monitorThread.notify()</td>
	</tr>
</table>
### Monitor Thread (on notify)
<table border="0">
	<tr>
		<td>event = concurrentQueue.poll()</td>
		<td>---&gt;</td>
		<td>Get handlers that can process the given event.<br/>Utilizes handler.handles(event).<br/>This collection of handlers<br/>is cached for later use.</td>
		<td>---&gt;</td>
		<td>handler.handle(event)<br/>(for each handler)</td>
	</tr>
</table>

Maven Usage
===========
Stable:
```xml
		<dependency>
			<groupId>com.strategicgains.domain-eventing</groupId>
			<artifactId>domain-eventing-core</artifactId>
			<version>0.4.1</version>
		</dependency>
```
OR (for hazelcast-clustered eventing):
```xml
		<dependency>
			<groupId>com.strategicgains.domain-eventing</groupId>
			<artifactId>domain-eventing-hazelcast</artifactId>
			<version>0.4.1</version>
		</dependency>
```
Development:
```xml
		<dependency>
			<groupId>com.strategicgains.domain-eventing</groupId>
			<artifactId>domain-eventing-core</artifactId>
			<version>0.4.2-SNAPSHOT</version>
		</dependency>
```
OR (for hazelcast-clustered eventing):
```xml
		<dependency>
			<groupId>com.strategicgains.domain-eventing</groupId>
			<artifactId>domain-eventing-hazelcast</artifactId>
			<version>0.4.2-SNAPSHOT</version>
		</dependency>
```

Or download the jar directly from: 
http://search.maven.org/#search%7Cga%7C1%7C%22domain-eventing%22

Note that to use the SNAPSHOT version, you must enable snapshots and a repository in your pom file as follows:
```xml
  <profiles>
    <profile>
       <id>allow-snapshots</id>
          <activation><activeByDefault>true</activeByDefault></activation>
       <repositories>
         <repository>
           <id>snapshots-repo</id>
           <url>https://oss.sonatype.org/content/repositories/snapshots</url>
           <releases><enabled>false</enabled></releases>
           <snapshots><enabled>true</enabled></snapshots>
         </repository>
       </repositories>
     </profile>
  </profiles>
```

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
### 0.4.1 - Released 16 Jan 2013
* Removed Ant build-related files
* Ensured Java 1.6 compatible artifact is released.

### 0.4.0 - Released 10 Jan 2013
* Introduced Maven build
* Released to Maven Central repository

### 0.3.0
* Introduced HazelCast for seamless intra-cluster (cross-node, multi-JVM) domain eventing.

### 0.2.0 - June 27, 2012
* Removed constraint of having to implement DomainEvent marker interface in event messages.
* Introduced EventQueue, allowing multiple EventMonitor threads to be processing events from the queue simultaneously.

### 0.1.0
* Initial release.
