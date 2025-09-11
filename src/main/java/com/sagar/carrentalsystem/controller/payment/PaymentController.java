package com.sagar.carrentalsystem.controller.payment;

import com.sagar.carrentalsystem.model.entity.payment.Payment;
import com.sagar.carrentalsystem.service.payment.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process/{bookingId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Payment> processPayment(@PathVariable Long bookingId,
                                                  @RequestParam String paymentMethod,
                                                  @RequestParam String transactionId) {
        Payment payment = paymentService.processPayment(bookingId, paymentMethod, transactionId);
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }
}
