package com.sagar.carrentalsystem.repository.userRepo;

import com.sagar.carrentalsystem.model.registerEntity.ValidEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ValidEmployeeRepository extends JpaRepository<ValidEmployee, String> {
    Optional<ValidEmployee> findByEmployeeId(String employeeId);
}
