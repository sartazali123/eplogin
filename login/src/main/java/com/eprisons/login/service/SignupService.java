package com.eprisons.login.service;

import com.eprisons.login.dto.*;
import com.eprisons.login.entity.AppUser;
import com.eprisons.login.entity.AppUser1;
import com.eprisons.login.exception.DuplicateUserException;
import com.eprisons.login.repository.UserRepository;

import com.eprisons.login.repository.UserRepository1;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class SignupService {

    private final UserRepository1 users;
    private final PasswordEncoder encoder;

    public SignupService(
            UserRepository1 users,
            PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Transactional
    public SignupResponse signup(SignupRequest request) {

        if (users.findByUsername(request.username()).isPresent()) {
            throw new DuplicateUserException(
                    "Username already exists");
        }

        AppUser1 user = new AppUser1();
        user.setUsername(request.username());
        user.setPassword(encoder.encode(request.password()));

        // DBA must approve before login
        user.setEnabled(false);

        // No roles assigned during signup
        user.setRoles(Set.of());

        AppUser1 saved = users.saveAndFlush(user);

        return new SignupResponse(
                saved.getId(),
                saved.getUsername(),
                Set.of(),
                "Registration submitted. Awaiting administrator approval."
        );
    }
}

