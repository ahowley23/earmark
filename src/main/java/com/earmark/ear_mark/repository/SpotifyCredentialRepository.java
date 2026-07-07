package com.earmark.ear_mark.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.earmark.ear_mark.model.SpotifyCredential;

@Repository
public interface SpotifyCredentialRepository extends JpaRepository<SpotifyCredential, Long> {

    // Find credentials by the user they belong to
    Optional<SpotifyCredential> findByUserId(Long userId);
}