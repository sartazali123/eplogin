package com.eprisons.login.security;

import com.eprisons.login.entity.AppUser;
import com.eprisons.login.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DemoDataConfig {

    @Bean
    @ConditionalOnProperty(
            name = "app.demo.enabled",
            havingValue = "true"
    )
    CommandLineRunner createUsers(
            UserRepository repository,
            PasswordEncoder encoder) {

        return args -> {

            createUser(repository, encoder,
                    "admin", "Admin@12345", "ADMIN");

            createUser(repository, encoder,
                    "officer", "Officer@12345", "OFFICER");

            createUser(repository, encoder,
                    "viewer", "Viewer@12345", "VIEWER");
        };
    }

    private void createUser(
            UserRepository repository,
            PasswordEncoder encoder,
            String username,
            String password,
            String role) {

        if (repository.findByUsername(username).isPresent()) {
            return;
        }

        AppUser user = new AppUser();

        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        user.setRole(role);
        user.setEnabled(true);

        repository.save(user);

        System.out.println("Created demo user: " + username);
    }
}

