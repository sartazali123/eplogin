package com.eprisons.login.service;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final long expiryMinutes;

    public JwtService(
            JwtEncoder encoder,
            @Value("${app.jwt.expiry-minutes}")
            long expiryMinutes) {

        this.encoder = encoder;
        this.expiryMinutes = expiryMinutes;
    }

    public String generate(Authentication authentication) {

        Instant now = Instant.now();

        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("eprisons-auth")
                .issuedAt(now)
                .expiresAt(now.plus(
                        expiryMinutes, ChronoUnit.MINUTES))
                .subject(authentication.getName())
                .claim("roles", roles)
                .build();

        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .build();

        return encoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }
}

