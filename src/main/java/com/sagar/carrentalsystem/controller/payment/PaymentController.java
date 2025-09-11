package com.sagar.carrentalsystem.controller.payment;

import com.sagar.carrentalsystem.model.dto.PaymentResponseDTO;
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
    public ResponseEntity<PaymentResponseDTO> processPayment(
            @PathVariable Long bookingId,
            @RequestParam String paymentMethod,
            @RequestParam String transactionId) {
        PaymentResponseDTO processedPayment = paymentService.processPayment(bookingId, paymentMethod, transactionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(processedPayment);
    }
}
