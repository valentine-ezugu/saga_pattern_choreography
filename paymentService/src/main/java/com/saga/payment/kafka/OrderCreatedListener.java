package com.saga.payment.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saga.payment.event.OrderCreatedEvent;
import com.saga.payment.service.PaymentProcessor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderCreatedListener {

    @Autowired
    private PaymentProcessor processor;

    @KafkaListener(topics = "order-topic", groupId = "payment-group")
    public void listen(ConsumerRecord<String, String> record) {
        try {
            OrderCreatedEvent event = new ObjectMapper().readValue(record.value(), OrderCreatedEvent.class);
            processor.handleOrderCreated(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
