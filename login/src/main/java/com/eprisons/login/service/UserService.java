//package com.eprisons.login.service;
//
//import com.eprisons.login.dto.CreateUserRequest;
//import com.eprisons.login.dto.UserResponse;
//import com.eprisons.login.entity.AppUser;
//import com.eprisons.login.exception.DuplicateUserException;
//import com.eprisons.login.repository.UserRepository;
//
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//public class UserService {
//
//    private final UserRepository repository;
//    private final PasswordEncoder encoder;
//
//    public UserService(
//            UserRepository repository,
//            PasswordEncoder encoder) {
//        this.repository = repository;
//        this.encoder = encoder;
//    }
//
//    @Transactional
//    public UserResponse createUser(CreateUserRequest request) {
//
//        if (repository.findByUsername(request.username())
//                .isPresent()) {
//            throw new DuplicateUserException(
//                    "Username already exists");
//        }
//
//        AppUser user = new AppUser();
//        user.setUsername(request.username());
//        user.setPassword(encoder.encode(request.password()));
//        user.setRole(request.role());
//        user.setEnabled(true);
//
//        try {
//            AppUser saved = repository.saveAndFlush(user);
//
//            return new UserResponse(
//                    saved.getId(),
//                    saved.getUsername(),
//                    saved.getRole(),
//                    saved.isEnabled()
//            );
//        } catch (DataIntegrityViolationException ex) {
//            throw new DuplicateUserException(
//                    "Username already exists");
//        }
//    }
//}
//


package com.eprisons.login.service;

import com.eprisons.login.dto.*;
import com.eprisons.login.entity.*;
import com.eprisons.login.exception.DuplicateUserException;
import com.eprisons.login.repository.*;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository1 userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    public UserService(
            UserRepository1 userRepository,
            RoleRepository roleRepository,
            PasswordEncoder encoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {

        if (userRepository.findByUsername(request.username())
                .isPresent()) {
            throw new DuplicateUserException(
                    "Username already exists");
        }

        Set<Role> roles = new HashSet<>(
                roleRepository.findByNameIn(request.roles())
        );

        if (roles.size() != request.roles().size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "One or more roles do not exist");
        }

        AppUser1 user = new AppUser1();
        user.setUsername(request.username());
        user.setPassword(encoder.encode(request.password()));
        user.setEnabled(true);
        user.setRoles(roles);

        AppUser1 saved = userRepository.saveAndFlush(user);

        return new UserResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()),
                saved.isEnabled()
        );
    }
}

