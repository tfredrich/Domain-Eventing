/*
    Copyright 2012, Strategic Gains, Inc.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package com.strategicgains.eventing.hazelcast;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

import com.hazelcast.core.ITopic;

/**
 * @author toddf
 * @since Oct 5, 2012
 */
public class HazelcastTopicQueueAdapter<T extends Serializable>
implements Queue<T>
{
	private ITopic<Object> topic;

	public HazelcastTopicQueueAdapter(ITopic<Object> iTopic)
	{
		super();
		this.topic = iTopic;
	}

    @Override
    public void clear()
    {
    	throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object o)
    {
    	throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
    	throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty()
    {
    	throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<T> iterator()
    {
    	throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o)
    {
    	throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
    	throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
    	throw new UnsupportedOperationException();
    }

    @Override
    public int size()
    {
    	throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray()
    {
    	throw new UnsupportedOperationException();
    }

    @Override
    public <P> P[] toArray(P[] a)
    {
    	throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(T object)
    {
    	topic.publish(object);
    	return true;
    }

    @Override
    public T element()
    {
    	throw new UnsupportedOperationException();
    }

    @Override
    public boolean offer(T e)
    {
    	throw new UnsupportedOperationException();
    }

    @Override
    public T peek()
    {
    	throw new UnsupportedOperationException();
    }

    @Override
    public T poll()
    {
    	throw new UnsupportedOperationException();
    }

    @Override
    public T remove()
    {
    	throw new UnsupportedOperationException();
    }

	/* (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
    @Override
    public boolean addAll(Collection<? extends T> c)
    {
    	throw new UnsupportedOperationException();
    }
}
