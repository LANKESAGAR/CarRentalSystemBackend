package com.sagar.carrentalsystem.mapper;

import com.sagar.carrentalsystem.model.dto.CarVariantDTO;
import com.sagar.carrentalsystem.model.entity.car.CarVariant;
import org.springframework.stereotype.Component;

@Component
public class CarVariantMapper {
    public CarVariant toEntity(CarVariantDTO dto) {
        CarVariant entity = new CarVariant();
        entity.setId(dto.getId());
        entity.setMake(dto.getMake());
        entity.setModel(dto.getModel());
        entity.setYear(dto.getYear());
        entity.setFuelType(dto.getFuelType());
        entity.setSeatingCapacity(dto.getSeatingCapacity());
        entity.setRentalRatePerHour(dto.getRentalRatePerHour());
        entity.setRentalRatePerDay(dto.getRentalRatePerDay());
        return entity;
    }
    public CarVariantDTO toDto(CarVariant entity) {
        CarVariantDTO dto = new CarVariantDTO();
        dto.setId(entity.getId());
        dto.setMake(entity.getMake());
        dto.setModel(entity.getModel());
        dto.setYear(entity.getYear());
        dto.setFuelType(entity.getFuelType());
        dto.setSeatingCapacity(entity.getSeatingCapacity());
        dto.setRentalRatePerHour(entity.getRentalRatePerHour());
        dto.setRentalRatePerDay(entity.getRentalRatePerDay());
        return dto;
    }
}
