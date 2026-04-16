package com.ecommerce.cart.service;

import com.ecommerce.cart.dto.request.LoginRequest;
import com.ecommerce.cart.dto.request.RegisterRequest;
import com.ecommerce.cart.dto.response.ApiResponse;
import com.ecommerce.cart.entity.jpa.User;
import com.ecommerce.cart.exception.custom.ConflictException;
import com.ecommerce.cart.repository.UserRepository;
import com.ecommerce.cart.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final MyUserDetailService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ApiResponse<String> login(LoginRequest request) {
    	log.info("Login attempt for username: {}", request.username());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        String token = jwtService.generateToken(userDetails);

        log.info("Login successful for username: {}", request.username());
        return ApiResponse.success("Login successful", token);
    }

    public ApiResponse<String> register(RegisterRequest request) {
    	log.info("Registration attempt for username: {}", request.username());

        if (userRepository.existsByUsername(request.username())) {
            log.warn("Registration failed - username already exists: {}", request.username());
        	throw new ConflictException("Username already exists");
        }
        
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .build();

        userRepository.save(user);
        
        log.info("User registered successfully: {}", request.username());

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        String token = jwtService.generateToken(userDetails);

        return ApiResponse.success("Registration successful", token);
    }
    
}