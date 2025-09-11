package com.sagar.carrentalsystem.model.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class BookingRequestDTO {
    private Long carVariantId;

    @Future(message = "Start date must be in the future.")
    private LocalDate startDate;

    @Future(message = "End date must be in the future.")
    private LocalDate endDate;

    public Long getCarVariantId() {
        return carVariantId;
    }

    public void setCarVariantId(Long carVariantId) {
        this.carVariantId = carVariantId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
