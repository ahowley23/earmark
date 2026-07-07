package com.earmark.ear_mark.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.earmark.ear_mark.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data generates the SQL for this automatically
    // based on the method name — finds a user by their email
    Optional<User> findByEmail(String email);

    // Used during registration to check if email is already taken
    boolean existsByEmail(String email);
}