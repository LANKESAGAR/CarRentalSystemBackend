package com.sagar.carrentalsystem.repository.carRepo;

import com.sagar.carrentalsystem.model.entity.car.CarVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarVariantRepository extends JpaRepository<CarVariant, Long> {
}
