package com.sagar.carrentalsystem.repository.userRepo;

import com.sagar.carrentalsystem.model.entity.user.RefreshToken;
import com.sagar.carrentalsystem.model.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
    Optional<RefreshToken> findByUser(User user);
}
