package com.saga.order.service;

import com.saga.order.model.SagaStep;
import com.saga.order.repository.SagaStepRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SagaLogger {

    @Autowired
    private SagaStepRepository repo;

    @Transactional
    public void log(Long orderId, String step, String status) {
        repo.save(new SagaStep(orderId, step, status));
    }
}
