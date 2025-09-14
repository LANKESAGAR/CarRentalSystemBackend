package com.sagar.carrentalsystem.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
@Component
public class JwtGenerator {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;
    @Value("${jwt.refreshExpirationMs}")
    private long refreshExpirationMs;
    Logger logger = LoggerFactory.getLogger(JwtGenerator.class);

    private SecretKey getSigningKey() {
        byte[] KeyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(KeyBytes);
    }

    public String generateToken(Authentication authentication) {
        logger.info("Generate Token for the user : {}", authentication.getName());
        String email = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("CUSTOMER");
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(currentDate)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String generateAccessToken(String email, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(String email, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpirationMs);

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String getEmailFromJwt(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public String getRoleFromJwt(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("role", String.class);
    }

    public boolean validateToken(String token, String expectedUserId) {
        logger.info("Validating the token with the UserId in our records");

        try {
            if (isTokenExpired(token)) {
                logger.error("Token is expired");
                throw new BadCredentialsException("Expired JWT token");
            }

            String userIdFromToken = getEmailFromJwt(token);

            if (!userIdFromToken.equals(expectedUserId)) {
                logger.error("User ID in token does not match");
                throw new BadCredentialsException("User ID in token does not match");
            }

            logger.info("Validation completed... User with the ID found in our records");
            return true;

        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
            throw new BadCredentialsException("Invalid JWT signature");
        } catch (io.jsonwebtoken.MalformedJwtException ex) {
            logger.error("Invalid JWT token format");
            throw new BadCredentialsException("Invalid JWT token format");
        } catch (io.jsonwebtoken.UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
            throw new BadCredentialsException("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty");
            throw new BadCredentialsException("JWT claims string is empty");
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiryTime = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
        return expiryTime.before(new Date());
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }

}
