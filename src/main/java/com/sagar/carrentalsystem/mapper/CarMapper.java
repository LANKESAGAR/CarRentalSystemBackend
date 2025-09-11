package com.sagar.carrentalsystem.mapper;

import com.sagar.carrentalsystem.model.dto.CarDTO;
import com.sagar.carrentalsystem.model.dto.CarVariantDTO;
import com.sagar.carrentalsystem.model.entity.car.Car;
import com.sagar.carrentalsystem.model.entity.car.CarVariant;
import org.springframework.stereotype.Component;

@Component
public class CarMapper {

    public CarDTO toCarDTO(Car car) {
        if (car == null) return null;

        CarVariant variant = car.getCarVariant();
        CarVariantDTO variantDTO = null;

        if (variant != null) {
            variantDTO = new CarVariantDTO(); // instantiate here!
            variantDTO.setId(variant.getId());
            variantDTO.setMake(variant.getMake());
            variantDTO.setModel(variant.getModel());
            variantDTO.setYear(variant.getYear());
            variantDTO.setFuelType(variant.getFuelType());
            variantDTO.setSeatingCapacity(variant.getSeatingCapacity());
            variantDTO.setRentalRatePerHour(variant.getRentalRatePerHour());
            variantDTO.setRentalRatePerDay(variant.getRentalRatePerDay());
        }

        CarDTO carDTO = new CarDTO();
        carDTO.setId(car.getId());
        carDTO.setLicensePlate(car.getLicensePlate());
        carDTO.setAvailable(car.isAvailable());
        carDTO.setLastServiceDate(car.getLastServiceDate());
        carDTO.setCarVariant(variantDTO);

        return carDTO;
    }
}

