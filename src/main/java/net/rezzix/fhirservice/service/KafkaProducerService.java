package net.rezzix.fhirservice.service;

import java.util.concurrent.ExecutionException;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public SendResult<String, String> sendMessage(String topic, String message) throws InterruptedException, ExecutionException {
        return kafkaTemplate.send(topic, message).get();
    }
}
