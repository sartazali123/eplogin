package com.eprisons.login.dto;


//public record UserResponse(
//        Long id,
//        String username,
//        String role,
//        boolean enabled
//) {}



import java.util.Set;

public record UserResponse(
        Long id,
        String username,
        Set<String> roles,
        boolean enabled
) {}


