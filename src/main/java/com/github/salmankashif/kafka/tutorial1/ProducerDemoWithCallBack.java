package com.github.salmankashif.kafka.tutorial1;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithCallBack {

    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(ProducerDemoWithCallBack.class);

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

        for (int i = 0; i < 5; i++) {

            // create Producer record
            ProducerRecord<String, String> record =
                    new ProducerRecord<String, String>("first_topic", "hello world" + Integer.toString(i));

            System.out.println("after creation of producer record");

            // send data - asynchronous
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    // executes every time a record is sucessfully sent or exception is thrown
                    if(e == null) {
                        // the record was successfully sent
                        logger.info("Received new metadata. \n "+
                                "Topic : " + recordMetadata.topic() + "\n" +
                                "Partition : " + recordMetadata.partition() + "\n" +
                                "Offset : " + recordMetadata.offset() + "\n" +
                                "Timestamp : " + recordMetadata.timestamp());
                    } else {

                        logger.error("Error while producing ", e);

                    }
                }
            });

        }


        // flush data
        producer.flush();
        // flush and close producer
        producer.close();
        System.out.println("after sending data");

    }

}
