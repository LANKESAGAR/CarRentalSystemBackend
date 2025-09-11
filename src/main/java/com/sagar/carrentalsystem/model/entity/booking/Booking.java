package com.sagar.carrentalsystem.model.entity.booking;

import com.sagar.carrentalsystem.constants.BookingStatus;
import com.sagar.carrentalsystem.model.entity.car.Car;
import com.sagar.carrentalsystem.model.entity.car.CarVariant;
import com.sagar.carrentalsystem.model.entity.user.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne
    @JoinColumn(name = "car_variant_id", nullable = false)
    private CarVariant carVariant;

    @ManyToOne
    @JoinColumn(name = "assigned_car_id") // This can be null initially
    private Car assignedCar;

    private LocalDate startDate;
    private LocalDate endDate;
    private double totalCost;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public CarVariant getCarVariant() {
        return carVariant;
    }

    public void setCarVariant(CarVariant carVariant) {
        this.carVariant = carVariant;
    }

    public Car getAssignedCar() {
        return assignedCar;
    }

    public void setAssignedCar(Car assignedCar) {
        this.assignedCar = assignedCar;
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
}
