package com.earmark.ear_mark.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "recommendations")
@Data
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many recommendations can belong to one user
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Many recommendations can point to one book
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // Which engine produced this recommendation
    // Matches the CHECK constraint in our SQL: 'RULE_BASED' or 'LLM'
    @Column(nullable = false, length = 20)
    private String engine;

    // Why this book was recommended — natural language explanation
    // from the rule engine or the LLM
    @Column(columnDefinition = "TEXT")
    private String reasoning;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}