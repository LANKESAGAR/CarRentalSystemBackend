package com.sagar.carrentalsystem.service.car;

import com.sagar.carrentalsystem.mapper.CarMapper;
import com.sagar.carrentalsystem.model.dto.CarDTO;
import com.sagar.carrentalsystem.model.entity.car.Car;
import com.sagar.carrentalsystem.model.entity.car.CarVariant;
import com.sagar.carrentalsystem.repository.carRepo.CarRepository;
import com.sagar.carrentalsystem.repository.carRepo.CarVariantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarVariantRepository carVariantRepository;

    @Autowired
    private CarMapper carMapper;

    public CarDTO addCar(Car car, Long carVariantId) {
        CarVariant carVariant = carVariantRepository.findById(carVariantId)
                .orElseThrow(() -> new EntityNotFoundException("Car variant not found with ID: " + carVariantId));
        car.setCarVariant(carVariant);
        car.setAvailable(true);
        Car savedCar = carRepository.save(car);
        return carMapper.toCarDTO(savedCar);
    }

    public List<CarDTO> getAllCars() {
        return carRepository.findAll().stream()
                .map(carMapper::toCarDTO)
                .toList();
    }
}

