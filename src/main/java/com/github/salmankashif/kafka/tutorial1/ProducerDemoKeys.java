package com.github.salmankashif.kafka.tutorial1;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ProducerDemoKeys {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Logger logger = LoggerFactory.getLogger(ProducerDemoWithCallBack.class);

        System.out.println("Starting to produce data");
        String bootStrpServer = "127.0.0.1:9092";
        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrpServer);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);


        for (int i = 0; i < 10; i++) {

            String topic = "first_topic";
            String value = "hello world " + Integer.toString(i);
            String key = "id_" + Integer.toString(i);

            // create Producer record
            ProducerRecord<String, String> record =
                    new ProducerRecord<String, String>(topic, key, value);

            logger.info("Key : " + key);

            // same key always goes to same partition
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
            }).get(); // block the send to make it synchronous - don't do it in production

        }


        // flush data
        producer.flush();
        // flush and close producer
        producer.close();
        System.out.println("after sending data");

    }

}
