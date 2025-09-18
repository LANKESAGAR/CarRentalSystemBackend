package com.sagar.carrentalsystem.controller.car;

import com.sagar.carrentalsystem.model.dto.CarVariantDTO;
import com.sagar.carrentalsystem.service.car.CarVariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/variants")
public class CarVariantController {
    @Autowired
    private CarVariantService carVariantService;


    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CarVariantDTO> createCarVariant(@RequestBody CarVariantDTO carVariant) {
        CarVariantDTO createdVariant = carVariantService.createCarVariant(carVariant);
        return new ResponseEntity<>(createdVariant, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CarVariantDTO>> getAllCarVariants() {
        List<CarVariantDTO> variants = carVariantService.getAllCarVariants();
        return new ResponseEntity<>(variants, HttpStatus.OK);
    }
}