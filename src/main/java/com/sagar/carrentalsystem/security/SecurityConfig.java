package com.sagar.carrentalsystem.security;

import com.sagar.carrentalsystem.service.security.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JwtAuthEntryPoint jwtAuthEntryPoint;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    @Value("${frontend.url}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        logger.info("Configuring Security Rules");
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(handling -> handling.authenticationEntryPoint(jwtAuthEntryPoint))
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
    // Allow all OPTIONS requests (preflight) without any authentication/authorization checks
    // This MUST come *before* any other requestMatchers that might restrict access.
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

    // Allow public access to authentication endpoints (POST requests for login/register)
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/api/variants/**").permitAll() // Publicly accessible to view cars
                                .requestMatchers("/api/admin/**").hasRole("ADMIN") // Only ADMIN can access /admin endpoints
    // Add a rule for customer-only endpoints
                                .requestMatchers("/api/customer/**").hasAnyRole("CUSTOMER", "ADMIN") // Both can access customer endpoints, but we'll mostly use CUSTOMER

    // All other requests require authentication
                                .anyRequest().authenticated()
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource())); // Apply CORS config

    // Add the JWT filter before Spring Security's default UsernamePasswordAuthenticationFilter
        httpSecurity.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(frontendUrl, "http://localhost:*")); // Match all localhost ports (React dev servers)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*")); // Include Authorization header
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
