package com.strategicgains.eventing.kafka;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

public class KafkaEventHandlerAdapter
implements Runnable
{
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final KafkaConsumer<String, String> consumer = null;

    public void run()
    {
        try
        {
            consumer.subscribe(Arrays.asList("topic"));

            while (!closed.get())
            {
                ConsumerRecords<String, String> records = consumer.poll(10000);
                // Handle new records
            }
        }
        catch (WakeupException e)
        {
            // Ignore exception if closing
            if (!closed.get()) throw e;
        }
        finally
        {
            consumer.close();
        }
    }

    // Shutdown hook which can be called from a separate thread
    public void shutdown()
    {
        closed.set(true);
        consumer.wakeup();
    }
}