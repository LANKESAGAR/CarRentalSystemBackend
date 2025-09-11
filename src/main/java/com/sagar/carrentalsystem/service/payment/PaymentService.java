package com.sagar.carrentalsystem.service.payment;

import com.sagar.carrentalsystem.constants.BookingStatus;
import com.sagar.carrentalsystem.model.entity.booking.Booking;
import com.sagar.carrentalsystem.model.entity.payment.Payment;
import com.sagar.carrentalsystem.repository.bookingRepo.BookingRepository;
import com.sagar.carrentalsystem.repository.paymentRepo.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private BookingRepository bookingRepository;


    public Payment processPayment(Long bookingId, String paymentMethod, String transactionId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with ID: " + bookingId));

        if (booking.getStatus() != BookingStatus.APPROVED) {
            throw new IllegalStateException("Booking is not in APPROVED status and cannot be paid.");
        }

        // Simulating a successful payment
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalCost());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod(paymentMethod);
        payment.setTransactionId(transactionId);

        // Update booking status to PAID
        booking.setStatus(BookingStatus.PAID);
        bookingRepository.save(booking);

        return paymentRepository.save(payment);
    }
}