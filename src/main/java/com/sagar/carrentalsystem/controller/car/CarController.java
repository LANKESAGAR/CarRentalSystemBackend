package com.sagar.carrentalsystem.controller.car;

import com.sagar.carrentalsystem.model.dto.CarDTO;
import com.sagar.carrentalsystem.model.entity.car.Car;
import com.sagar.carrentalsystem.service.car.CarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cars")
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping("/{carVariantId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CarDTO> addCar(@PathVariable Long carVariantId, @Valid @RequestBody Car car) {
        CarDTO addedCar = carService.addCar(car, carVariantId);
        return new ResponseEntity<>(addedCar, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<CarDTO>> getAllCars() {
        List<CarDTO> cars = carService.getAllCars();
        return new ResponseEntity<>(cars, HttpStatus.OK);
    }
}
