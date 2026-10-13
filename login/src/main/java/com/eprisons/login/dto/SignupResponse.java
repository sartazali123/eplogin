package com.eprisons.login.dto;



import java.util.Set;

public record SignupResponse(
        Long id,
        String username,
        Set<String> roles,
        String message
) {}

