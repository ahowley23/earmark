package com.earmark.ear_mark.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

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
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private SpotifyCredential spotifyCredential;

    @PrePersist
    // Runs automatically before a new row is inserted
    // Sets createdAt so we never forget to populate it
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}