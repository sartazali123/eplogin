package com.eprisons.login.security;


import com.eprisons.login.entity.AppUser1;
import com.eprisons.login.entity.Role;
import com.eprisons.login.exception.CustomAccessDeniedHandler;
import com.eprisons.login.exception.JwtAuthenticationEntryPoint;
import com.eprisons.login.repository.UserRepository;
import org.springframework.http.HttpMethod;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import com.eprisons.login.repository.UserRepository1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Configuration
public class SecurityConfig {

//    @Bean
//    public UserDetailsService userDetailsService(
//            UserRepository repository) {
//
//        return username -> {
//            var user = repository.findByUsername(username)
//                    .orElseThrow(() ->
//                            new UsernameNotFoundException(
//                                    "Invalid credentials"));
//
//            return User.builder()
//                    .username(user.getUsername())
//                    .password(user.getPassword())
//                    .roles(user.getRole())
//                    .disabled(!user.isEnabled())
//                    .build();
//        };
//    }

    @Bean
    public UserDetailsService userDetailsService(
            UserRepository1 repository) {

        return username -> {

            AppUser1 user = repository.findByUsername(username)
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "Invalid username or password"));

            String[] roles = user.getRoles()
                    .stream()
                    .map(Role::getName)
                    .toArray(String[]::new);

            return User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles(roles)
                    .disabled(!user.isEnabled())
                    .build();
        };
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder encoder) {

        var provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(encoder);

        return new ProviderManager(provider);
    }

    @Bean
    public SecretKey jwtSecretKey(
            @Value("${app.jwt.secret-base64}") String encoded) {

        byte[] bytes = Base64.getDecoder().decode(encoded);

        if (bytes.length < 32) {
            throw new IllegalArgumentException(
                    "JWT secret must be at least 32 bytes");
        }

        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey key) {
        return new NimbusJwtEncoder(
                new ImmutableSecret<>(key));
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey key) {
        return NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

//    @Bean
//    public JwtAuthenticationConverter jwtAuthenticationConverter() {
//
//        var granted = new JwtGrantedAuthoritiesConverter();
//        granted.setAuthoritiesClaimName("roles");
//        granted.setAuthorityPrefix("ROLE_");
//
//        var converter = new JwtAuthenticationConverter();
//        converter.setJwtGrantedAuthoritiesConverter(granted);
//
//        return converter;
//    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter granted =
                new JwtGrantedAuthoritiesConverter();

        granted.setAuthoritiesClaimName("roles");
        granted.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(granted);

        return converter;
    }

//
//    @Bean
//    public SecurityFilterChain securityFilterChain(
//            HttpSecurity http,
//            JwtAuthenticationConverter converter)
//            throws Exception {
//
//        http
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(
//                                SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//
//                        .requestMatchers(
//                                "/api/auth/login",
//                                "/api/auth/signup"
//                        ).permitAll()
//
//                        .requestMatchers("/api/admin/**")
//                        .hasRole("ADMIN")
//
//                        .requestMatchers("/api/officer/**")
//                        .hasAnyRole("ADMIN", "OFFICER")
//
//                        .requestMatchers("/api/viewer/**")
//                        .hasAnyRole(
//                                "ADMIN", "OFFICER", "VIEWER")
//
//                        .anyRequest().authenticated()
//                )
//                .oauth2ResourceServer(oauth ->
//                        oauth.jwt(jwt ->
//                                jwt.jwtAuthenticationConverter(converter)));
//
//        return http.build();
//    }


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter converter,
            JwtAuthenticationEntryPoint entryPoint,
            CustomAccessDeniedHandler deniedHandler)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(deniedHandler))

                .authorizeHttpRequests(auth -> auth

                        // Public endpoints
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/signup",
                                "/api/auth/login"
                        ).permitAll()

                        // Protected endpoints
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/officer/**")
                        .hasAnyRole("ADMIN", "OFFICER")

                        .requestMatchers("/api/viewer/**")
                        .hasAnyRole("ADMIN", "OFFICER", "VIEWER")

                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth -> oauth
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(deniedHandler)
                        .jwt(jwt ->
                                jwt.jwtAuthenticationConverter(converter)
                        )
                );

        return http.build();
    }




}

