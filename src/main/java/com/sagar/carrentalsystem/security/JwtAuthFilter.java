package com.sagar.carrentalsystem.security;

import com.sagar.carrentalsystem.service.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JwtGenerator jwtGenerator;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = getJwtFromRequest(request);

            if (StringUtils.hasText(token)) {
                logger.info("Token is present");

                String userEmail = jwtGenerator.getEmailFromJwt(token);

                if (org.apache.commons.lang3.StringUtils.isNotBlank(userEmail)
                        && SecurityContextHolder.getContext().getAuthentication() == null) {

                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);

                    if (jwtGenerator.validateToken(token, userDetails.getUsername())) {
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities());

                        authenticationToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        logger.info("Authentication set in SecurityContext for user: {}", userEmail);
                    } else {
                        logger.warn("JWT token is invalid for user: {}", userEmail);
                    }
                }
            } else {
                logger.info("JWT token is not present in Authorization header.");
            }

        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }


    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
