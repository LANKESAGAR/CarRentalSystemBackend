package com.sagar.carrentalsystem.mapper;

import com.sagar.carrentalsystem.model.dto.BookingResponseDTO;
import com.sagar.carrentalsystem.model.entity.booking.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {
    public BookingResponseDTO toDto(Booking booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(booking.getId());
        dto.setCustomerName(booking.getCustomer().getUsername());
        dto.setCarVariantModel(booking.getCarVariant().getMake() + " " + booking.getCarVariant().getModel());
        dto.setCarVariantId(booking.getCarVariant().getId());
        if (booking.getAssignedCar() != null) {
            dto.setAssignedCarLicensePlate(booking.getAssignedCar().getLicensePlate());
        }
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        dto.setTotalCost(booking.getTotalCost());
        dto.setStatus(booking.getStatus());
        return dto;
    }
}
