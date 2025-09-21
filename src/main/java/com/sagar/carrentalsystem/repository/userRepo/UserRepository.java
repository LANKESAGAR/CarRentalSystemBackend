package com.sagar.carrentalsystem.repository.userRepo;

import com.sagar.carrentalsystem.constants.Role;
import com.sagar.carrentalsystem.model.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    // Add this method
/*
    Optional<User> findByRole(Role role);
*/
    boolean existsByEmail(String email);
    Optional<User> findByEmployeeId(String employeeId);
    List<User> findByRole(Role role);


}
