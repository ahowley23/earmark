package com.earmark.ear_mark.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data  // Lombok: generates getters, setters, equals, hashCode, toString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // IDENTITY means the database generates the ID (BIGSERIAL in our SQL)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // One user has one set of Spotify credentials
    // mappedBy = "user" means SpotifyCredential owns the foreign key column
    // CascadeType.ALL means save/delete operations cascade to credentials too
    
    @JsonIgnoreProperties("user")
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private SpotifyCredential spotifyCredential;

    @PrePersist
    // Runs automatically before a new row is inserted
    // Sets createdAt so we never forget to populate it
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}