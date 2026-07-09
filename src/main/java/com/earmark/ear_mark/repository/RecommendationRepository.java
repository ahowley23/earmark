package com.earmark.ear_mark.repository;

import com.earmark.ear_mark.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    // Get all recommendations for a user, newest first
    List<Recommendation> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Check if we have recent recommendations — used to avoid
    // calling Claude again if we generated some recently
    List<Recommendation> findByUserIdAndCreatedAtAfter(Long userId, LocalDateTime after);

    // Get recommendations by engine type for the comparison view
    List<Recommendation> findByUserIdAndEngine(Long userId, String engine);
}