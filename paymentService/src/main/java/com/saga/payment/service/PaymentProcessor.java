package com.saga.payment.service;

import com.saga.payment.event.*;
import com.saga.payment.model.Payment;
import com.saga.payment.model.PaymentStatus;
import com.saga.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
public class PaymentProcessor {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private SagaLogger sagaLogger;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private final Random random = new Random();

    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        sagaLogger.log(event.getOrderId(), "PAYMENT_ATTEMPTED", "SUCCESS");

        Payment payment = new Payment();
        payment.setOrderId(event.getOrderId());
        payment.setAmount(event.getAmount());

        boolean success = random.nextBoolean();
        if (success) {
            sagaLogger.log(event.getOrderId(), "PAYMENT_COMPLETED", "SUCCESS");
            payment.setStatus(PaymentStatus.SUCCESS);
            kafkaTemplate.send("payment-success-topic", new PaymentSuccessEvent(event.getOrderId()));
        } else {
            sagaLogger.log(event.getOrderId(), "PAYMENT_FAILED", "FAILED");
            payment.setStatus(PaymentStatus.FAILED);
            kafkaTemplate.send("payment-failed-topic", new PaymentFailedEvent(event.getOrderId()));
        }

        paymentRepository.save(payment);
    }
}
