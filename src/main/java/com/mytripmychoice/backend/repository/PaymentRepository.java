package com.mytripmychoice.backend.repository;

import com.mytripmychoice.backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findByRazorpayOrderId(String orderId);
}