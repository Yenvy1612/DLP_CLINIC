package com.acare.backend.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acare.backend.dto.ResponseDTO;
import com.acare.backend.entity.User;
import com.acare.backend.repository.UserRepository;
import com.acare.backend.service.ActivityLogService;
import com.acare.backend.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository repo;

    private final ActivityLogService service;

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = repo.findAll();
        users.sort(Comparator.comparing((User u) -> u.getRole()));
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> getUserById(@PathVariable Long id) {
        Optional<User> user = repo.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/doctor")
    public ResponseEntity<List<User>> getDoctors() {
        return ResponseEntity.ok(repo.findByRole("DOCTOR"));
    }

    @GetMapping("/patient")
    public ResponseEntity<List<User>> getPatients() {
        return ResponseEntity.ok(repo.findByRole("PATIENT"));
    }

    /* Cập nhật thông tin */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        Optional<User> existing = repo.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = existing.get();

        // Cập nhật các trường cơ bản
        if (updatedUser.getFullName() != null) user.setFullName(updatedUser.getFullName());
        if (updatedUser.getEmail() != null) user.setEmail(updatedUser.getEmail());
        if (updatedUser.getPhone() != null) user.setPhone(updatedUser.getPhone());
        if (updatedUser.getGender() != null) user.setGender(updatedUser.getGender());
        if (updatedUser.getBirthDate() != null) user.setBirthDate(updatedUser.getBirthDate());
        if (updatedUser.getAddress() != null) user.setAddress(updatedUser.getAddress());
        if (updatedUser.getIdNumber() != null) user.setIdNumber(updatedUser.getIdNumber());
        if (updatedUser.getRole() != null) user.setRole(updatedUser.getRole());
        /* Đổi mật khẩu */
        if (updatedUser.getPasswordHash() != null && !updatedUser.getPasswordHash().isBlank()) {
            user.setPasswordHash(updatedUser.getPasswordHash());
        }

        User saved = repo.save(user);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> addUser(@RequestBody User user) {
        if (repo.findByEmail(user.getEmail()).isPresent()) return ResponseEntity.ok(new ResponseDTO(404, false, "EMAIL WAS USED", user));
        else if (repo.findByPhone(user.getPhone()).isPresent()) return ResponseEntity.ok(new ResponseDTO(404, false, "NUMBER WAS USED", user));
        else if (repo.findByIdNumber(user.getIdNumber()).isPresent()) return ResponseEntity.ok(new ResponseDTO(404, false, "ID NUMBER WAS USED", user));
        User savedUser = repo.save(user);
        return ResponseEntity.ok(new ResponseDTO(201, true, "SIGN UP SUCCESSFULLY", savedUser));
    } 

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteUser(@PathVariable Long id) {
        Optional<User> user = repo.findById(id);
        repo.deleteById(id);
        service.add("USER MANAGEMENT", "ADMIN dã xóa tài khoản của người dùng " + user.get().getFullName());
        return ResponseEntity.ok(new ResponseDTO(200, true, "DELETED SUCCESSFULLY", null));
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String email) {
        
        List<User> users = userService.searchUsers(fullName, role, email);
        users.sort(Comparator.comparing(User::getRole));
        
        return ResponseEntity.ok(users);
    }
}