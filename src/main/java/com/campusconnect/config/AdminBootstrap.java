package com.campusconnect.config;

import com.campusconnect.entity.User;
import com.campusconnect.enums.Role;
import com.campusconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminBootstrap {

    @Bean
    CommandLineRunner createConfiguredAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${ADMIN_EMAIL:}") String adminEmail,
            @Value("${ADMIN_PASSWORD:}") String adminPassword) {
        return args -> {
            if (adminEmail.isBlank() || adminPassword.isBlank()
                    || userRepository.findByEmail(adminEmail.trim().toLowerCase()).isPresent()) {
                return;
            }
            userRepository.save(new User(
                    "Campus Administrator",
                    adminEmail.trim().toLowerCase(),
                    passwordEncoder.encode(adminPassword),
                    Role.ADMIN));
        };
    }
}
