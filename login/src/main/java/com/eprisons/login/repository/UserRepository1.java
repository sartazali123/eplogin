package com.eprisons.login.repository;



import com.eprisons.login.entity.AppUser1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

public interface UserRepository1
        extends JpaRepository<AppUser1, Long> {

    @EntityGraph(attributePaths = "roles")
    Optional<AppUser1> findByUsername(String username);
}

