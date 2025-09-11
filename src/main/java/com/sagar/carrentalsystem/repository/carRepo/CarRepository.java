package com.sagar.carrentalsystem.repository.carRepo;

import com.sagar.carrentalsystem.model.entity.car.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    Car findByLicensePlate(String licensePlate);
    List<Car> findByCarVariant_Id(Long carVariantId);
}
