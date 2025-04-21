package com.saga.order.repository;

import com.saga.order.model.SagaStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SagaStepRepository extends JpaRepository<SagaStep, Long> {
    List<SagaStep> findByOrderId(Long id);
}
