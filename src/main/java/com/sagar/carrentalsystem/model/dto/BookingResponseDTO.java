package com.sagar.carrentalsystem.model.dto;


import com.sagar.carrentalsystem.constants.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public class BookingResponseDTO {
    private Long id;
    private String customerName;
    private String carVariantModel;
    private Long carVariantId;
    private String assignedCarLicensePlate;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalCost;
    private BookingStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCarVariantModel() {
        return carVariantModel;
    }

    public void setCarVariantModel(String carVariantModel) {
        this.carVariantModel = carVariantModel;
    }

    public String getAssignedCarLicensePlate() {
        return assignedCarLicensePlate;
    }

    public void setAssignedCarLicensePlate(String assignedCarLicensePlate) {
        this.assignedCarLicensePlate = assignedCarLicensePlate;
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

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public Long getCarVariantId() {
        return carVariantId;
    }

    public void setCarVariantId(Long carVariantId) {
        this.carVariantId = carVariantId;
    }
}
