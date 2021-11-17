package com.github.salmankashif.kafka.tutorial1;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class ProducerDemo {

    public static void main(String[] args) {
        System.out.println("Starting to produce data");
        String bootStrpServer = "127.0.0.1:9092";
        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrpServer);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        System.out.println("After setting properties");
        // create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        System.out.println("after creation of kafka producer");
        // create Producer record
        ProducerRecord<String, String> record =
                new ProducerRecord<String, String>("first_topic", "hello world");

        System.out.println("after creation of producer record");

        // send data - asynchronous
        producer.send(record);

        // flush data
        producer.flush();
        // flush and close producer
        producer.close();
        System.out.println("after sending data");

    }

}
