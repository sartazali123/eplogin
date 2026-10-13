package com.eprisons.login.dto;


import jakarta.validation.constraints.*;
import java.util.Set;

public record CreateUserRequest(

        @NotBlank
        @Size(min = 3, max = 100)
        String username,

        @NotBlank
        @Size(min = 12, max = 72)
        String password,

        @NotEmpty
        Set<@Pattern(
                regexp = "ADMIN|OFFICER|VIEWER"
        ) String> roles

) {}

