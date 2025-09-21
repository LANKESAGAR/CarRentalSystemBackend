package com.sagar.carrentalsystem.controller.auth;

import com.sagar.carrentalsystem.constants.Role;
import com.sagar.carrentalsystem.model.entity.user.User;
import com.sagar.carrentalsystem.model.registerEntity.ValidEmployee;
import com.sagar.carrentalsystem.model.request.AuthRequest;
import com.sagar.carrentalsystem.repository.userRepo.UserRepository;
import com.sagar.carrentalsystem.repository.userRepo.ValidEmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/admin")
public class AdminController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ValidEmployeeRepository validEmployeeRepository;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    public AdminController(UserRepository userRepository, PasswordEncoder passwordEncoder, ValidEmployeeRepository validEmployeeRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.validEmployeeRepository = validEmployeeRepository;
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> registerNewAdmin(@RequestBody AuthRequest registerRequest) {
    // Step 1: Check if the employeeId is valid and an admin
        ValidEmployee validEmployee = validEmployeeRepository.findByEmployeeId(registerRequest.getEmployeeId())
                .orElse(null);

        if (validEmployee == null || !validEmployee.isAdmin()) {
            logger.warn("Attempt to register admin with invalid or non-admin employee ID: {}", registerRequest.getEmployeeId());
            return new ResponseEntity<>("Invalid employee ID or not authorized to be an admin.", HttpStatus.BAD_REQUEST);
        }

    // Step 2: Check if an account already exists for this employeeId
        if (userRepository.findByEmployeeId(registerRequest.getEmployeeId()).isPresent()) {
            return new ResponseEntity<>("An admin account for this employee ID already exists!", HttpStatus.BAD_REQUEST);
        }

    // Step 3: Create the user account
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setUsername(registerRequest.getUsername());
        user.setRole(Role.ADMIN);
        user.setEmployeeId(validEmployee.getEmployeeId());

        userRepository.save(user);

        return new ResponseEntity<>("New admin registered successfully!", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteAdmin(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return new ResponseEntity<>("Admin with ID " + id + " not found.", HttpStatus.NOT_FOUND);
        }

        userRepository.deleteById(id);

        logger.info("Admin with ID {} was deleted.", id);
        return new ResponseEntity<>("Admin deleted successfully.", HttpStatus.OK);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<User>> getAllAdmins() {
        List<User> admins = userRepository.findByRole(Role.ADMIN);
        return new ResponseEntity<>(admins, HttpStatus.OK);
    }

}