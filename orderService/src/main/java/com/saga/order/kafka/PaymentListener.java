package com.saga.order.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saga.order.event.PaymentFailedEvent;
import com.saga.order.event.PaymentSuccessEvent;
import com.saga.order.service.OrderService;
import com.saga.order.service.SagaLogger;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentListener {

    @Autowired
    private OrderService orderService;

    @Autowired
    private SagaLogger sagaLogger;
    @KafkaListener(topics = "payment-failed-topic", groupId = "order-group")
    public void handleFailure(ConsumerRecord<String, String> record) {
        try {
            PaymentFailedEvent event = new ObjectMapper().readValue(record.value(), PaymentFailedEvent.class);
            Long orderId = event.getOrderId();

            sagaLogger.log(orderId, "PAYMENT_FAILED", "FAILED");

            // Cancel the order (compensation)
            orderService.cancelOrder(orderId);

            // ✅ Log ORDER_COMPENSATED
            sagaLogger.log(orderId, "ORDER_COMPENSATED", "SUCCESS");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @KafkaListener(topics = "payment-success-topic", groupId = "order-group")
    public void handlePaymentSuccess(ConsumerRecord<String, String> record) {
        try {
            PaymentSuccessEvent event = new ObjectMapper().readValue(record.value(), PaymentSuccessEvent.class);
            Long orderId = event.getOrderId();

            sagaLogger.log(orderId, "PAYMENT_SUCCESS", "SUCCESS");

            orderService.completeOrder(orderId);

            //NO Log ORDER_COMPENSATED needed

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
