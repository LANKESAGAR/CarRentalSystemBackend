package com.sagar.carrentalsystem.mapper;

import com.sagar.carrentalsystem.model.dto.PaymentResponseDTO;
import com.sagar.carrentalsystem.model.entity.payment.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {
    public PaymentResponseDTO toDTO(Payment payment) {

        if (payment == null) {
            return null;
        }

        PaymentResponseDTO paymentResponseDTO = new PaymentResponseDTO();
        paymentResponseDTO.setId(paymentResponseDTO.getId());
        paymentResponseDTO.setAmount(payment.getAmount());
        paymentResponseDTO.setPaymentMethod(payment.getPaymentMethod());
        paymentResponseDTO.setTransactionId(payment.getTransactionId());
        paymentResponseDTO.setBookingId(payment.getBooking().getId());
        paymentResponseDTO.setCustomerEmail(payment.getBooking().getCustomer().getEmail());
        paymentResponseDTO.setCarModel(payment.getBooking().getCarVariant().getModel());

        return paymentResponseDTO;
    }
}
