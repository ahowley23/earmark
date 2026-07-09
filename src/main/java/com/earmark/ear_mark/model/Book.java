package com.earmark.ear_mark.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "books")
@Data
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The Open Library ID — e.g. "OL7353617M"
    // unique because we never want duplicate book entries
    @Column(name = "open_library_id", nullable = false, unique = true, length = 50)
    private String openLibraryId;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(length = 255)
    private String author;

    // Stored as JSON — a book can have multiple genres
    // e.g. ["literary fiction", "dystopian", "political"]
    @Column(columnDefinition = "jsonb")
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    private String genres;
    
    @Column(name = "cover_url", columnDefinition = "TEXT")
    private String coverUrl;
}