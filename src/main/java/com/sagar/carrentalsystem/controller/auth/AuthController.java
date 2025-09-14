package com.sagar.carrentalsystem.controller.auth;

import com.sagar.carrentalsystem.constants.Role;
import com.sagar.carrentalsystem.model.entity.user.ChangePasswordRequest;
import com.sagar.carrentalsystem.model.entity.user.User;
import com.sagar.carrentalsystem.model.request.AuthRequest;
import com.sagar.carrentalsystem.model.response.AuthResponse;
import com.sagar.carrentalsystem.repository.userRepo.UserRepository;
import com.sagar.carrentalsystem.security.JwtGenerator;
import com.sagar.carrentalsystem.service.security.RefreshTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.sagar.carrentalsystem.model.entity.user.RefreshToken;


import java.security.Principal;
import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtGenerator jwtGenerator;
    private RefreshTokenService refreshTokenService;
    Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtGenerator jwtGenerator, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping(path="register")
    public ResponseEntity<String> registerUser(@RequestBody AuthRequest registerRequest){
        if(userRepository.existsByEmail(registerRequest.getEmail())){
            return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
        }
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setUsername(registerRequest.getUsername());
        user.setRole(Role.CUSTOMER);

        userRepository.save(user);

        return new ResponseEntity<>("User registered successfully!", HttpStatus.OK);
    }


    /***
     * Login Request is validated against DB using Authentication Manager
     * with UsernamePasswordAuthenticationToken will call the CustomUserDetailsService
     * @param loginRequest
     * @return
     */
    @PostMapping(path="/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody AuthRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get the UserDetails from the authenticated object
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Retrieve the full User entity to access the role and employeeId
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if the requested role matches the user's actual role
        if (!user.getRole().name().equalsIgnoreCase(loginRequest.getRole())) {
        // If the roles don't match, return an error
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // If the selected role is ADMIN, check the employee ID
        if (user.getRole() == Role.ADMIN) {
            if (!user.getEmployeeId().equals(loginRequest.getEmployeeId())) {
        // If employee IDs don't match, reject the login
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }


        // If all checks pass, generate and return the token
        String token = jwtGenerator.generateToken(authentication);
        String refreshToken = jwtGenerator.generateRefreshToken(user.getEmail(), user.getRole().name());

        refreshTokenService.storeRefreshToken(user.getEmail(), refreshToken);
        return new ResponseEntity<>(new AuthResponse(token, refreshToken), HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestParam String refreshToken) {
        RefreshToken validToken = refreshTokenService.validateRefreshToken(refreshToken);

        String email = jwtGenerator.getEmailFromJwt(validToken.getToken());
        String role = jwtGenerator.getRoleFromJwt(validToken.getToken());


        String newAccessToken = jwtGenerator.generateAccessToken(email, role);
        String newRefreshToken = jwtGenerator.generateRefreshToken(email, role);

        refreshTokenService.storeRefreshToken(email, newRefreshToken);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(newAccessToken);
        authResponse.setRefreshToken(newRefreshToken);
        authResponse.setTokenType("Bearer");

        return ResponseEntity.ok(authResponse);
    }


    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request, Principal principal) {
        // Step 1: Get the currently authenticated user's email from the Principal object
        String userEmail = principal.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Step 2: Verify the old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return new ResponseEntity<>("Incorrect old password.", HttpStatus.BAD_REQUEST);
        }

        // Step 3: Check if the new password is the same as the old one
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            return new ResponseEntity<>("New password cannot be the same as the old one.", HttpStatus.BAD_REQUEST);
        }

        // Step 4: Encode and update the new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return new ResponseEntity<>("Password changed successfully.", HttpStatus.OK);
    }


}
