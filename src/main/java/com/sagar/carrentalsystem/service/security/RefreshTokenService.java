package com.sagar.carrentalsystem.service.security;

import com.sagar.carrentalsystem.model.entity.user.RefreshToken;
import com.sagar.carrentalsystem.model.entity.user.User;
import com.sagar.carrentalsystem.repository.userRepo.RefreshTokenRepository;
import com.sagar.carrentalsystem.repository.userRepo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.refreshExpirationMs}")
    private Long refreshExpirationMs;

    /**
     * Stores or updates a refresh token for the user.
     */
    public void storeRefreshToken(String email, String token) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Find existing refresh token for the user
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElse(new RefreshToken());

        // Set/Update token details
        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExpirationMs));

        refreshTokenRepository.save(refreshToken);
    }

    /**
     * Validates the refresh token and returns it if valid.
     */
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            // Optional: Delete expired token from DB
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token has expired");
        }

        return refreshToken;
    }

    /**
     * Optional: Remove refresh token for a user (e.g., logout)
     */
    public void deleteRefreshToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);
    }
}
