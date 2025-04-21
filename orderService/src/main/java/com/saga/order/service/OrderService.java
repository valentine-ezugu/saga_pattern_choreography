package com.saga.order.service;


import com.saga.order.event.OrderCreatedEvent;
import com.saga.order.model.Order;
import com.saga.order.model.OrderStatus;
import com.saga.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SagaLogger sagaLogger;

    @Autowired
    private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    @Transactional
    public Order createOrder(Order order) {

        order.setStatus(OrderStatus.CREATED);
        Order saved = orderRepository.save(order);
        sagaLogger.log(order.getId(), "ORDER_CREATED", "SUCCESS");

        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(saved.getId());
        event.setAmount(saved.getAmount());
        event.setProduct(saved.getProduct());

        kafkaTemplate.send("order-topic", event);

        return saved;
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        });
    }

    @Transactional
    public void completeOrder(Long orderId) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);
        });
    }
}
