package com.eprisons.login.dto;



import jakarta.validation.constraints.*;

public record SignupRequest(
        @NotBlank
        @Size(min = 3, max = 100)
        String username,

        @NotBlank
        @Size(min = 12, max = 72)
        String password
) {}

