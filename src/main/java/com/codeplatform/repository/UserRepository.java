package com.codeplatform.repository;

import com.codeplatform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email (for signup validation)
    Optional<User> findByEmail(String email);

    // Find user by id and name (for login validation)
    Optional<User> findByIdAndName(Long id, String name);

    // Check if email already exists
    boolean existsByEmail(String email);
}