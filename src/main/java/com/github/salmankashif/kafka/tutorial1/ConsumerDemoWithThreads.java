package com.github.salmankashif.kafka.tutorial1;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ConsumerDemoWithThreads {

    public static void main(String[] args) {

        new ConsumerDemoWithThreads().run();

    }

    private ConsumerDemoWithThreads() {

    }

    public void run() {
        Logger logger = LoggerFactory.getLogger(ConsumerDemoWithThreads.class.getName());

        String bootStrpServer = "127.0.0.1:9092";
        String groupId = "my-sixth-application";
        String topic = "first_topic";

        // latch for dealing with multiple threads
        CountDownLatch latch = new CountDownLatch(1);

        // create the consumer runnable
        logger.info("Creating the consumer thread");
        Runnable myConsumerRunable = new ConsumerRunnable(
                bootStrpServer,
                groupId,
                topic,
                latch
        );

        // start the thread
        Thread myThread = new Thread(myConsumerRunable);
        myThread.start();

        // add a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(
                () -> {
                    logger.info("Caugth shutdown hook");
                    ((ConsumerRunnable) myConsumerRunable).shutdown();
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    logger.info("Application has exited");
                }
        ));

        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("Application got interrupted", e);
            e.printStackTrace();
        } finally {
            logger.info("Application is closing");
        }

    }

    public class ConsumerRunnable implements Runnable {

        CountDownLatch latch;
        private Logger logger = LoggerFactory.getLogger(ConsumerRunnable.class.getName());

        private KafkaConsumer<String, String> consumer;
        public ConsumerRunnable(String bootStrpServer,
                                String groupId,
                                String topic,
                                CountDownLatch latch) {
            this.latch = latch;

            // create consumer configs
            Properties properties = new Properties();
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrpServer);
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            // create consumer
            consumer = new KafkaConsumer<String, String>(properties);

            // subscribe consumer to a topic(s)
            consumer.subscribe(Arrays.asList(topic));

        }

        @Override
        public void run() {

            // poll for new data
            try {
                while (true) {
                    ConsumerRecords<String, String> records =
                            consumer.poll(Duration.ofMillis(100)); // new in KAFKA 2.0.0

                    for(ConsumerRecord<String, String> record : records) {
                        logger.info("Key : " + record.key() + ", Value : " + record.value());
                        logger.info("Partition : " + record.partition() + ", Offset : " + record.offset());
                    }
                }
            } catch (WakeupException e) {
                logger.info("Received shutdown signal!");
            } finally {
                consumer.close();
                // tell our main code we are done with the consumer
                latch.countDown();
            }

        }

        public void shutdown() {
            // wakeup method is a special method to interrupt consumer.poll()
            // it will throw the exception WakeUpException
            consumer.wakeup();
        }
    }

}
