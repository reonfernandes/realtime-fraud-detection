package com.reon.titan_backend.service.impl;

import com.reon.titan_backend.common.UniqueIdGenerator;
import com.reon.titan_backend.document.User;
import com.reon.titan_backend.document.type.Role;
import com.reon.titan_backend.document.type.TokenType;
import com.reon.titan_backend.dto.SignInRequest;
import com.reon.titan_backend.dto.SignUpRequest;
import com.reon.titan_backend.dto.response.SignInResponse;
import com.reon.titan_backend.dto.response.SignUpResponse;
import com.reon.titan_backend.exception.EmailAlreadyExistsException;
import com.reon.titan_backend.jwt.JwtService;
import com.reon.titan_backend.mapper.AuthMapper;
import com.reon.titan_backend.repository.UserRepository;
import com.reon.titan_backend.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final AuthMapper authMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final Integer ACCESS_TOKEN_EXPIRY;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder encoder,
            AuthMapper authMapper,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            @Value("${security.jwt.access-token-expiry}") Integer accessTokenExpiry
    ) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.authMapper = authMapper;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        ACCESS_TOKEN_EXPIRY = accessTokenExpiry;
    }

    @Override
    public SignUpResponse generateUser(SignUpRequest signUpRequest) {
        log.info("Processing new registration request:........{}", signUpRequest.email());
        if (userRepository.existsByEmail(signUpRequest.email())) {
            log.warn("Signup attempt with existing email: {}", signUpRequest.email());
            throw new EmailAlreadyExistsException("Account already exists for this email");
        }

        User user = authMapper.mapToEntity(signUpRequest);
        user.setId(UniqueIdGenerator.uniqueIdGenerator());
        user.setPassword(encoder.encode(signUpRequest.password()));
        user.setRoles(Set.of(Role.USER));
        user.setCreatedAt(Instant.now());

        try {
            User savedUser = userRepository.insert(user);
            log.info("Registration complete for user id: {}", savedUser.getId());
            return authMapper.responseToUser(savedUser);
        } catch (DuplicateKeyException exception) {
            log.warn("Email got registered by another request: {}", signUpRequest.email());
            throw new EmailAlreadyExistsException("Account already exists for this email");
        }
    }

    @Override
    public SignInResponse authenticateUser(SignInRequest signInRequest) {
        log.info("Processing sign in request:........{}", signInRequest.email());

        // this one line does the whole check: loads the user through CustomUserDetailService,
        // bcrypt-matches the password, and verifies the account is enabled.
        // it THROWS on failure (BadCredentialsException / DisabledException) - it never returns false.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.email(),
                        signInRequest.password()
                )
        );

        // the principal is whatever CustomUserDetailService returned, which is our User document
        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        log.info("Sign in success for user id: {}", user.getId());

        return new SignInResponse(
                token,
                TokenType.ACCESS,
                ACCESS_TOKEN_EXPIRY,
                Instant.now()
        );
    }
}
