package com.sagar.carrentalsystem.model.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class CarDTO {
    private Long id;

    @NotBlank(message = "License plate must not be empty")
    @Size(max = 15, message = "License plate must be at most 15 characters")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "License plate can contain only uppercase letters, numbers, and hyphens")
    private String licensePlate;

    private boolean isAvailable;

    @PastOrPresent(message = "Last service date cannot be in the future")
    private LocalDate lastServiceDate;

    private CarVariantDTO carVariant;

    // Constructors
    public CarDTO() {}

    public CarDTO(Long id, String licensePlate, boolean isAvailable, LocalDate lastServiceDate, CarVariantDTO carVariant) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.isAvailable = isAvailable;
        this.lastServiceDate = lastServiceDate;
        this.carVariant = carVariant;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public LocalDate getLastServiceDate() { return lastServiceDate; }
    public void setLastServiceDate(LocalDate lastServiceDate) { this.lastServiceDate = lastServiceDate; }
    public CarVariantDTO getCarVariant() { return carVariant; }
    public void setCarVariant(CarVariantDTO carVariant) { this.carVariant = carVariant; }
}

