package com.sagar.carrentalsystem.configuration;

import com.sagar.carrentalsystem.constants.Role;
import com.sagar.carrentalsystem.model.entity.user.User;
import com.sagar.carrentalsystem.repository.userRepo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class AdminSeeder {
    private static final Logger logger = LoggerFactory.getLogger(AdminSeeder.class);

    @Bean
    public CommandLineRunner createAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
    // This is the hardcoded ID that must exist in valid_employees table
            String initialAdminEmployeeId = "ADMIN1";

            if (userRepository.findByEmployeeId(initialAdminEmployeeId).isEmpty()) {
                logger.info("Initial admin user not found. Creating a new admin user...");
                User admin = new User();
                admin.setEmail("admin@test.com");
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("adminpassword"));
                admin.setRole(Role.ADMIN);
                admin.setEmployeeId(initialAdminEmployeeId);

                userRepository.save(admin);
                logger.info("Initial admin user created successfully with email: admin@test.com");
            } else {
                logger.info("Initial admin user already exists. Skipping creation.");
            }
        };
    }
}

