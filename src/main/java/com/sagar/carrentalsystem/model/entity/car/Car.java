package com.sagar.carrentalsystem.model.entity.car;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "car_variant_id", nullable = false)
    private CarVariant carVariant;

    @Column(unique = true, nullable = false)
    private String licensePlate;

    private boolean isAvailable;
    private LocalDate lastServiceDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CarVariant getCarVariant() {
        return carVariant;
    }

    public void setCarVariant(CarVariant carVariant) {
        this.carVariant = carVariant;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public LocalDate getLastServiceDate() {
        return lastServiceDate;
    }

    public void setLastServiceDate(LocalDate lastServiceDate) {
        this.lastServiceDate = lastServiceDate;
    }
}
