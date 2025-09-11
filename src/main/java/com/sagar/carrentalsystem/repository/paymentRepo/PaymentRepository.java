package com.sagar.carrentalsystem.repository.paymentRepo;

import com.sagar.carrentalsystem.model.entity.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
