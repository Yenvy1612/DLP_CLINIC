package com.acare.backend.controller;

import com.acare.backend.entity.User;
import com.acare.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository repo;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("username");
        String password = body.get("password");

        //Tìm user trong DB
        User user = repo.findByEmail(email).orElse(null);
        if (user == null || !user.getPasswordHash().equals(password)) {
            return ResponseEntity.status(401).body(Map.of("error", "Sai tài khoản hoặc mật khẩu"));
        }

        //Chuẩn hóa role
        String originalRole = user.getRole();
        String normalizedRole = originalRole.equals("ADMIN") ? "ADMIN" : "USER";

        //Trả về kết quả
        return ResponseEntity.ok(Map.of(
                "username", user.getEmail(),
                "roles", new String[]{normalizedRole},
                "originalRole", originalRole,
                "id", user.getId()
        ));
    }
}
