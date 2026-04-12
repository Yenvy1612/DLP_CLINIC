package com.acare.backend.controller;

import java.util.Comparator;
import java.util.List;

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

import com.acare.backend.dto.ApiResponse;
import com.acare.backend.dto.user.DoctorProfileResponse;
import com.acare.backend.dto.user.DoctorProfileUpdateRequest;
import com.acare.backend.dto.user.UserCreateRequest;
import com.acare.backend.entity.User;
import com.acare.backend.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getUsers(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String email) {

        if ((fullName != null && !fullName.isBlank())
                || (role != null && !role.isBlank())
                || (email != null && !email.isBlank())) {
            List<User> users = userService.searchUsers(fullName, role, email);
            users.sort(Comparator.comparing(User::getRole));
            return ResponseEntity.ok(users);
        }

        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/{id}/doctor-profile")
    public ResponseEntity<DoctorProfileResponse> getDoctorProfileByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getDoctorProfileByUserId(id));
    }

    @PutMapping("/{id}/doctor-profile")
    public ResponseEntity<DoctorProfileResponse> updateDoctorProfileByUserId(
            @PathVariable Long id,
            @RequestBody DoctorProfileUpdateRequest request) {
        return ResponseEntity.ok(userService.updateDoctorProfileByUserId(id, request));
    }

    @GetMapping("/doctor")
    public ResponseEntity<List<User>> getDoctors() {
        return ResponseEntity.ok(userService.getDoctors());
    }

    @GetMapping("/patient")
    public ResponseEntity<List<User>> getPatients() {
        return ResponseEntity.ok(userService.getPatients());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updateUser) {
        return ResponseEntity.ok(userService.updateUser(id, updateUser));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> addUser(@Valid @RequestBody UserCreateRequest request) {
        return createUser(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String email) {
        return getUsers(fullName, role, email);
    }
}