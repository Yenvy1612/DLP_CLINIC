package com.acare.backend.security;

import com.acare.backend.config.AppSecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final AppSecurityProperties securityProperties;

    public String generateAccessToken(String subject, List<String> roles) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(securityProperties.getJwt().getExpirationMinutes(), ChronoUnit.MINUTES);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("acare-backend")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(subject)
                .claim("roles", roles)
                .claim("token_type", "ACCESS")
                .build();

        JwsHeader headers = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue();
    }

    public String generateRefreshToken(String subject, Long userId) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(securityProperties.getJwt().getRefreshExpirationDays(), ChronoUnit.DAYS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("acare-backend")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(subject)
                .claim("user_id", userId)
                .claim("token_type", "REFRESH")
                .build();

        JwsHeader headers = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue();
    }

    public Jwt decode(String token) {
        return jwtDecoder.decode(token);
    }

    public long getAccessExpirationSeconds() {
        return securityProperties.getJwt().getExpirationMinutes() * 60;
    }

    public long getRefreshExpirationSeconds() {
        return securityProperties.getJwt().getRefreshExpirationDays() * 24 * 60 * 60;
    }
}
