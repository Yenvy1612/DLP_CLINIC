package com.acare.backend.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.acare.backend.config.BootstrapAdminProperties;
import com.acare.backend.entity.User;
import com.acare.backend.entity.enums.UserRole;
import com.acare.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminBootstrapRunner implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BootstrapAdminProperties props;
    private final ActivityLogService activityLogService;

    @Override
    public void run(ApplicationArguments args) {
        if (!props.isEnabled()) {
            log.info("Admin bootstrap is disabled by configuration.");
            return;
        }

        if (userRepository.existsByRole(UserRole.ADMIN)) {
            return;
        }

        if (isBlank(props.getEmail()) || isBlank(props.getPassword()) || isBlank(props.getFullName())) {
            log.warn("Cannot bootstrap admin because required properties are missing.");
            return;
        }

        if (userRepository.findByEmailIgnoreCase(props.getEmail()).isPresent()) {
            log.warn("Cannot bootstrap admin because email {} is already used.", props.getEmail());
            return;
        }

        User admin = User.createAdmin(
            props.getFullName(),
            props.getEmail(),
            props.getPhone(),
            props.getIdNumber(),
            props.getAddress(),
            encodePassword(props.getPassword()));

        User saved = userRepository.save(admin);
        activityLogService.add("BOOTSTRAP", "Created initial admin account: " + saved.getEmail());
        log.warn("Initial ADMIN account created: {}. Please change the password immediately.", saved.getEmail());
    }

    private String encodePassword(String rawPassword) {
        if (rawPassword.startsWith("{")) {
            return rawPassword;
        }
        return passwordEncoder.encode(rawPassword);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
