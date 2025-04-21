package com.saga.payment.event;

public class PaymentFailedEvent {
    private Long orderId;

    public PaymentFailedEvent() {}
    public PaymentFailedEvent(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
