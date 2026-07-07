package com.earmark.ear_mark.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "taste_profiles")
@Data
public class TasteProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One user has one taste profile
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // JSONB in Postgres — stored as a JSON string in Java
    // When we build the taste engine this gets parsed into a real object
    @Column(name = "genre_weights", columnDefinition = "jsonb")
    private String genreWeights;

    @Column(name = "top_artists", columnDefinition = "jsonb")
    private String topArtists;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    // Runs on both insert and update — keeps updated_at current
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}