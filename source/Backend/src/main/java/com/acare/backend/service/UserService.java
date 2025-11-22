package com.acare.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.acare.backend.entity.User;
import com.acare.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> searchUsers(String fullName, String role, String email) {
        // Lấy tất cả users
        List<User> users = userRepository.findAll();

        // Search theo tên người dùng
        if (fullName != null && !fullName.trim().isEmpty()) {
            users = users.stream()
                    .filter(user -> user.getFullName() != null && 
                            user.getFullName().toLowerCase().contains(fullName.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Search theo vai trò
        if (role != null && !role.trim().isEmpty()) {
            users = users.stream()
                    .filter(user -> user.getRole().equalsIgnoreCase(role))
                    .collect(Collectors.toList());
        }

        // Search theo email
        if (email != null && !email.trim().isEmpty()) {
            users = users.stream()
                    .filter(user -> user.getEmail() != null && 
                            user.getEmail().toLowerCase().contains(email.toLowerCase()))
                    .collect(Collectors.toList());
        }
        return users;
    }
}
