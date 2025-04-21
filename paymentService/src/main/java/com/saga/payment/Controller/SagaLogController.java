package com.saga.payment.Controller;

import com.saga.payment.model.SagaStep;
import com.saga.payment.repository.SagaStepRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/saga-steps")
public class SagaLogController {

    @Autowired
    private SagaStepRepository sagaStepRepository;

    @GetMapping("/{orderId}")
    public List<SagaStep> getSteps(@PathVariable Long orderId) {
        return sagaStepRepository.findByOrderId(orderId);
    }
}
