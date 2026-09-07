package com.reon.titan_backend.config;

import com.reon.titan_backend.common.UniqueIdGenerator;
import com.reon.titan_backend.document.User;
import com.reon.titan_backend.document.type.Role;
import com.reon.titan_backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.EnumSet;

/**
 * Every signup gets the USER role, so without this there is no way to create the first admin
 * and the admin only endpoints cannot be used.
 * Runs on startup. If the email or password is not set it does nothing, so you can still
 * promote an account by hand in the database instead.
 */
@Component
@Slf4j
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final String adminEmail;
    private final String adminPassword;

    public AdminSeeder(UserRepository userRepository,
                       PasswordEncoder encoder,
                       @Value("${security.admin.email:}") String adminEmail,
                       @Value("${security.admin.password:}") String adminPassword) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            log.info("Admin email or password not set, skipping admin creation");
            return;
        }

        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin account already exists, nothing to do");
            return;
        }

        User admin = User.builder()
                .id(UniqueIdGenerator.uniqueIdGenerator())
                .email(adminEmail)
                .password(encoder.encode(adminPassword))
                .roles(EnumSet.of(Role.USER, Role.ADMIN))
                .createdAt(Instant.now())
                .build();

        userRepository.insert(admin);
        log.info("Admin account created for: {}", adminEmail);
    }
}
