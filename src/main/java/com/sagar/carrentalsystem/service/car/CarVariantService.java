package com.sagar.carrentalsystem.service.car;

import com.sagar.carrentalsystem.mapper.CarVariantMapper;
import com.sagar.carrentalsystem.model.dto.CarVariantDTO;
import com.sagar.carrentalsystem.model.entity.car.CarVariant;
import com.sagar.carrentalsystem.repository.carRepo.CarVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarVariantService {
    @Autowired
    private CarVariantRepository carVariantRepository;

    @Autowired
    private CarVariantMapper carVariantMapper;

    public CarVariantDTO createCarVariant(CarVariantDTO carVariantDTO) {
        CarVariant carVariant = carVariantMapper.toEntity(carVariantDTO);
        CarVariant createdCarVariant = carVariantRepository.save(carVariant);
        return carVariantMapper.toDto(createdCarVariant);
    }

    public List <CarVariantDTO> getAllCarVariants() {
        List <CarVariant> variants = carVariantRepository.findAll();
        return variants.stream().map(variant ->
                carVariantMapper.toDto(variant)).collect(Collectors.toList());
    }
}
