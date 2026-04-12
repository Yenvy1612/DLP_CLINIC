package com.acare.backend.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import com.acare.backend.config.AppSecurityProperties;
import com.acare.backend.dto.ApiResponse;
import com.acare.backend.dto.auth.AuthResponse;
import com.acare.backend.dto.auth.ChangePasswordRequest;
import com.acare.backend.dto.auth.LoginRequest;
import com.acare.backend.dto.auth.RegisterRequest;
import com.acare.backend.dto.auth.UpdateProfileRequest;
import com.acare.backend.dto.auth.UserProfileResponse;
import com.acare.backend.dto.user.DoctorProfileResponse;
import com.acare.backend.dto.user.DoctorProfileUpdateRequest;
import com.acare.backend.entity.PatientProfile;
import com.acare.backend.entity.RefreshToken;
import com.acare.backend.entity.User;
import com.acare.backend.entity.enums.Gender;
import com.acare.backend.entity.enums.UserRole;
import com.acare.backend.exception.BadRequestException;
import com.acare.backend.repository.PatientProfileRepository;
import com.acare.backend.repository.RefreshTokenRepository;
import com.acare.backend.repository.UserRepository;
import com.acare.backend.security.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AppSecurityProperties securityProperties;
    private final UserService userService;

    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmailIgnoreCase(request.getUsername())
                .orElseThrow(() -> new BadRequestException("Sai tai khoan hoac mat khau"));

        validateUserEnabled(user);

        if (!matchesPassword(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Sai tai khoan hoac mat khau");
        }

        issueAuthCookies(user, response);
        return buildAuthResponse(user);
    }

    @Transactional
    public ApiResponse<User> register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("PASSWORD MISMATCH");
        }

        validateRegisterUniqueFields(request);

        User newUser = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.PATIENT)
                .gender(parseGenderOrDefault(request.getGender()))
                .birthDate(parseBirthDate(request.getBirthDate()))
                .address(request.getAddress())
                .idNumber(request.getIdNumber())
                .enabled(true)
                .build()
                .withDefaults();

        User saved = userRepository.save(newUser);
        if (!patientProfileRepository.existsByUserId(saved.getId())) {
            patientProfileRepository.save(PatientProfile.createForUser(saved.getId()));
        }

        return ApiResponse.created("SIGN UP SUCCESSFULLY", saved);
    }

    @Transactional
    public AuthResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshCookieName = securityProperties.getCookie().getRefreshTokenName();
        String rawRefreshToken = readCookieValue(request, refreshCookieName);
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            throw new BadRequestException("Khong tim thay refresh token");
        }

        Jwt refreshJwt = decodeRefreshJwt(rawRefreshToken);
        Long tokenUserId = extractUserId(refreshJwt);
        User user = userRepository.findById(tokenUserId)
                .orElseThrow(() -> new BadRequestException("Nguoi dung khong ton tai"));

        validateUserEnabled(user);
        if (!user.getEmail().equalsIgnoreCase(refreshJwt.getSubject())) {
            throw new BadRequestException("Refresh token khong hop le");
        }

        String tokenHash = hashToken(rawRefreshToken);
        RefreshToken persistedToken = refreshTokenRepository.findByTokenHashAndRevokedFalse(tokenHash)
                .orElseThrow(() -> new BadRequestException("Refresh token khong hop le"));

        if (!persistedToken.getUserId().equals(user.getId())) {
            throw new BadRequestException("Refresh token khong hop le");
        }

        LocalDateTime now = LocalDateTime.now();
        if (persistedToken.getExpiresAt().isBefore(now)) {
            refreshTokenRepository.save(persistedToken.revoke(now));
            throw new BadRequestException("Refresh token da het han");
        }

        refreshTokenRepository.save(persistedToken.revoke(now));

        issueAuthCookies(user, response);
        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse me(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadRequestException("Nguoi dung khong ton tai"));
        return user.toProfileResponse();
    }

    @Transactional
    public UserProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadRequestException("Nguoi dung khong ton tai"));
        validateUserEnabled(user);

        User updated = user.toBuilder()
                .fullName(resolveUpdatedText(request.getFullName(), user.getFullName()))
                .email(resolveUpdatedText(request.getEmail(), user.getEmail()))
                .phone(resolveUpdatedText(request.getPhone(), user.getPhone()))
                .gender(request.getGender() != null ? parseGenderOrDefault(request.getGender()) : user.getGender())
                .birthDate(request.getBirthDate() != null ? parseBirthDate(request.getBirthDate()) : user.getBirthDate())
                .address(resolveUpdatedText(request.getAddress(), user.getAddress()))
                .idNumber(resolveUpdatedText(request.getIdNumber(), user.getIdNumber()))
                .build()
                .withDefaults();

        if (updated.getFullName() == null || updated.getFullName().isBlank()) {
            throw new BadRequestException("Ho ten khong duoc de trong");
        }
        if (updated.getEmail() == null || updated.getEmail().isBlank()) {
            throw new BadRequestException("Email khong duoc de trong");
        }

        validateUniqueFieldsForProfileUpdate(updated, user.getId());
        User saved = userRepository.save(updated);
        return saved.toProfileResponse();
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadRequestException("Nguoi dung khong ton tai"));
        validateUserEnabled(user);

        if (!matchesPassword(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Mat khau hien tai khong dung");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Xac nhan mat khau moi khong khop");
        }

        if (matchesPassword(request.getNewPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Mat khau moi phai khac mat khau cu");
        }

        userRepository.save(user.withEncodedPassword(passwordEncoder.encode(request.getNewPassword())));

        refreshTokenRepository.revokeAllByUserId(user.getId(), LocalDateTime.now());
        clearAuthCookies(response);
    }

    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = readCookieValue(request, securityProperties.getCookie().getRefreshTokenName());
        if (refreshToken != null && !refreshToken.isBlank()) {
            String tokenHash = hashToken(refreshToken);
            refreshTokenRepository.findByTokenHashAndRevokedFalse(tokenHash).ifPresent(token -> {
                refreshTokenRepository.save(token.revoke(LocalDateTime.now()));
            });
        }
        clearAuthCookies(response);
    }

    @Transactional(readOnly = true)
    public DoctorProfileResponse getMyDoctorProfile(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadRequestException("Nguoi dung khong ton tai"));
        validateUserEnabled(user);
        return userService.getDoctorProfileByUserId(user.getId());
    }

    @Transactional
    public DoctorProfileResponse updateMyDoctorProfile(String email, DoctorProfileUpdateRequest request) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadRequestException("Nguoi dung khong ton tai"));
        validateUserEnabled(user);
        return userService.updateDoctorProfileByUserId(user.getId(), request);
    }

    private void issueAuthCookies(User user, HttpServletResponse response) {
        String role = user.getRole().name();
        String accessToken = jwtService.generateAccessToken(user.getEmail(), List.of(role));
        String refreshToken = jwtService.generateRefreshToken(user.getEmail(), user.getId());

        RefreshToken persistedToken = RefreshToken.issueFor(
            user.getId(),
            hashToken(refreshToken),
            LocalDateTime.now().plusSeconds(jwtService.getRefreshExpirationSeconds()));
        refreshTokenRepository.save(persistedToken);

        setCookie(response,
                securityProperties.getCookie().getAccessTokenName(),
                accessToken,
                jwtService.getAccessExpirationSeconds());
        setCookie(response,
                securityProperties.getCookie().getRefreshTokenName(),
                refreshToken,
                jwtService.getRefreshExpirationSeconds());
    }

    private void clearAuthCookies(HttpServletResponse response) {
        setCookie(response, securityProperties.getCookie().getAccessTokenName(), "", 0);
        setCookie(response, securityProperties.getCookie().getRefreshTokenName(), "", 0);
    }

    private void setCookie(HttpServletResponse response, String name, String value, long maxAgeSeconds) {
        String cookiePath = securityProperties.getCookie().getPath();
        if (cookiePath == null || cookiePath.isBlank()) {
            cookiePath = "/";
        }

        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(securityProperties.getCookie().isSecure())
                .path(cookiePath)
                .sameSite(securityProperties.getCookie().getSameSite())
                .maxAge(maxAgeSeconds);

        String domain = securityProperties.getCookie().getDomain();
        if (domain != null && !domain.isBlank()) {
            builder.domain(domain);
        }

        response.addHeader(HttpHeaders.SET_COOKIE, builder.build().toString());
    }

    private AuthResponse buildAuthResponse(User user) {
        String role = user.getRole().name();
        return AuthResponse.builder()
            .username(user.getEmail())
            .roles(new String[]{role})
            .originalRole(role)
            .id(user.getId())
            .tokenType("Bearer")
            .expiresInSeconds(jwtService.getAccessExpirationSeconds())
            .build();
    }

    private void validateUserEnabled(User user) {
        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new BadRequestException("Tai khoan da bi vo hieu hoa");
        }
    }

    private Jwt decodeRefreshJwt(String refreshToken) {
        try {
            Jwt jwt = jwtService.decode(refreshToken);
            String tokenType = jwt.getClaimAsString("token_type");
            if (!"REFRESH".equals(tokenType)) {
                throw new BadRequestException("Refresh token khong hop le");
            }
            return jwt;
        } catch (JwtException ex) {
            throw new BadRequestException("Refresh token khong hop le");
        }
    }

    private Long extractUserId(Jwt refreshJwt) {
        Number claimValue = refreshJwt.getClaim("user_id");
        if (claimValue == null) {
            throw new BadRequestException("Refresh token khong hop le");
        }
        return claimValue.longValue();
    }

    private String readCookieValue(HttpServletRequest request, String cookieName) {
        if (request == null || cookieName == null || cookieName.isBlank()) {
            return null;
        }
        var cookie = WebUtils.getCookie(request, cookieName);
        if (cookie == null) {
            return null;
        }
        return cookie.getValue();
    }

    private boolean matchesPassword(String rawPassword, String storedHash) {
        if (storedHash == null || storedHash.isBlank()) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, storedHash);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Khong the hash refresh token", ex);
        }
    }

    private void validateUniqueFieldsForProfileUpdate(User user, Long currentUserId) {
        userRepository.findByEmailIgnoreCase(user.getEmail())
                .filter(found -> !found.getId().equals(currentUserId))
                .ifPresent(found -> {
                    throw new BadRequestException("EMAIL WAS USED");
                });

        if (user.getPhone() != null && !user.getPhone().isBlank()) {
            userRepository.findByPhone(user.getPhone())
                    .filter(found -> !found.getId().equals(currentUserId))
                    .ifPresent(found -> {
                        throw new BadRequestException("NUMBER WAS USED");
                    });
        }

        if (user.getIdNumber() != null && !user.getIdNumber().isBlank()) {
            userRepository.findByIdNumber(user.getIdNumber())
                    .filter(found -> !found.getId().equals(currentUserId))
                    .ifPresent(found -> {
                        throw new BadRequestException("ID NUMBER WAS USED");
                    });
        }
    }

    private String resolveUpdatedText(String incomingValue, String currentValue) {
        if (incomingValue == null) {
            return currentValue;
        }
        String trimmed = incomingValue.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void validateRegisterUniqueFields(RegisterRequest request) {
        userRepository.findByEmailIgnoreCase(request.getEmail())
                .ifPresent(found -> {
                    throw new BadRequestException("EMAIL WAS USED");
                });

        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            userRepository.findByPhone(request.getPhone())
                    .ifPresent(found -> {
                        throw new BadRequestException("NUMBER WAS USED");
                    });
        }

        if (request.getIdNumber() != null && !request.getIdNumber().isBlank()) {
            userRepository.findByIdNumber(request.getIdNumber())
                    .ifPresent(found -> {
                        throw new BadRequestException("ID NUMBER WAS USED");
                    });
        }
    }

    private Gender parseGenderOrDefault(String rawGender) {
        if (rawGender == null || rawGender.isBlank()) {
            return Gender.OTHER;
        }

        try {
            return Gender.valueOf(rawGender.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Gender khong hop le");
        }
    }

    private java.time.LocalDate parseBirthDate(String rawBirthDate) {
        if (rawBirthDate == null || rawBirthDate.isBlank()) {
            return null;
        }

        try {
            return java.time.LocalDate.parse(rawBirthDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException ignored) {
        }

        try {
            return java.time.LocalDate.parse(rawBirthDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("Ngay sinh khong hop le");
        }
    }
}
