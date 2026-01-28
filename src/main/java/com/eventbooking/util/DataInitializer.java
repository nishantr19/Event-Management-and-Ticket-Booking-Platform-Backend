package com.eventbooking.util;

import com.eventbooking.entity.User;
import com.eventbooking.enums.Role;
import com.eventbooking.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Create admin user if not exists
        if (!userRepository.existsByEmail("admin@eventbooking.com")) {
            User admin = User.builder()
                    .email("admin@eventbooking.com")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Admin User")
                    .phoneNumber("1234567890")
                    .role(Role.ADMIN)
                    .active(true)
                    .build();
            userRepository.save(admin);
            System.out.println("✅ Admin user created: admin@eventbooking.com / admin123");
        }
    }
}