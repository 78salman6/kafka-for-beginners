package com.github.salmankashif.kafka.tutorial1;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerDemoGroups {

    public static void main(String[] args) {

        String bootStrpServer = "127.0.0.1:9092";
        String groupId = "my-fifth-application";
        String topic = "first_topic";

        Logger logger = LoggerFactory.getLogger(ConsumerDemo.class.getName());

        // create consumer configs
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrpServer);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // create consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

        // subscribe consumer to a topic(s)
        consumer.subscribe(Arrays.asList("first_topic"));

        // poll for new data
        while (true) {
            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(100)); // new in KAFKA 2.0.0

            for(ConsumerRecord<String, String> record : records) {
                logger.info("Key : " + record.key() + ", Value : " + record.value());
                logger.info("Partition : " + record.partition() + ", Offset : " + record.offset());
            }
        }
    }

}

