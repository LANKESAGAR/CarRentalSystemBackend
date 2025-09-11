package com.sagar.carrentalsystem.model.dto;

import jakarta.validation.constraints.*;

public class CarVariantDTO {
    private Long id;
    @NotBlank(message = "Make is required")
    private String make;
    @NotBlank(message = "Model is required")
    private String model;
    @Min(value = 1900, message = "Year must be a valid car year")
    private int year;
    @NotBlank(message = "Fuel type is required")
    private String fuelType;
    @Min(value = 1, message = "Seating capacity must be at least 1")
    private int seatingCapacity;
    @Min(value = 0, message = "Rental rate per hour cannot be negative")
    private double rentalRatePerHour;
    @Min(value = 0, message = "Rental rate per day cannot be negative")
    private double rentalRatePerDay;

    // Constructors
    public CarVariantDTO() {}

    public CarVariantDTO(Long id, String make, String model, int year, String fuelType,
                         int seatingCapacity, double rentalRatePerHour, double rentalRatePerDay) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.fuelType = fuelType;
        this.seatingCapacity = seatingCapacity;
        this.rentalRatePerHour = rentalRatePerHour;
        this.rentalRatePerDay = rentalRatePerDay;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    public int getSeatingCapacity() { return seatingCapacity; }
    public void setSeatingCapacity(int seatingCapacity) { this.seatingCapacity = seatingCapacity; }
    public double getRentalRatePerHour() { return rentalRatePerHour; }
    public void setRentalRatePerHour(double rentalRatePerHour) { this.rentalRatePerHour = rentalRatePerHour; }
    public double getRentalRatePerDay() { return rentalRatePerDay; }
    public void setRentalRatePerDay(double rentalRatePerDay) { this.rentalRatePerDay = rentalRatePerDay; }
}

