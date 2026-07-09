package com.earmark.ear_mark.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.earmark.ear_mark.dto.BookRecommendationDto;
import com.earmark.ear_mark.model.Book;
import com.earmark.ear_mark.model.Recommendation;
import com.earmark.ear_mark.model.User;
import com.earmark.ear_mark.repository.BookRepository;
import com.earmark.ear_mark.repository.RecommendationRepository;
import com.earmark.ear_mark.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final SpotifyService spotifyService;
    private final ClaudeService claudeService;

    public List<BookRecommendationDto.RecommendationResponse> getOrGenerateRecommendations(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check for recommendations in the last 24 hours
        List<Recommendation> cached = recommendationRepository
                .findByUserIdAndCreatedAtAfter(
                        user.getId(),
                        LocalDateTime.now().minusHours(24)
                );

        if (!cached.isEmpty()) {
            System.out.println("Returning " + cached.size() + " cached recommendations");
            return toResponseList(cached);
        }

        System.out.println("No cached recommendations — generating new ones");

        // Get top artists from Spotify
        var artists = spotifyService.getTopArtists(email, "medium_term");
        System.out.println("Got " + artists.size() + " artists from Spotify");

        // Call Claude
        BookRecommendationDto claudeResponse = claudeService.getBookRecommendations(artists);

        if (claudeResponse == null || claudeResponse.getRecommendations() == null) {
            System.err.println("Claude returned null recommendations");
            return List.of();
        }

        System.out.println("Claude returned " + claudeResponse.getRecommendations().size() + " recommendations");

        List<Recommendation> saved = new ArrayList<>();

        for (BookRecommendationDto.Recommendation rec : claudeResponse.getRecommendations()) {
            try {
                // Find or create book
                Book book = bookRepository
                        .findByTitleAndAuthor(rec.getTitle(), rec.getAuthor())
                        .orElseGet(() -> {
                            Book newBook = new Book();
                            newBook.setTitle(rec.getTitle());
                            newBook.setAuthor(rec.getAuthor());
                            newBook.setGenres(null);
                            newBook.setOpenLibraryId(
                                    "claude-" + rec.getTitle().toLowerCase()
                                            .replaceAll("[^a-z0-9]", "-")
                            );
                            Book b = bookRepository.save(newBook);
                            System.out.println("Saved book: " + b.getTitle() + " with ID: " + b.getId());
                            return b;
                        });

                // Save recommendation
                Recommendation recommendation = new Recommendation();
                recommendation.setUser(user);
                recommendation.setBook(book);
                recommendation.setEngine("LLM");
                recommendation.setReasoning(rec.getReasoning());

                Recommendation r = recommendationRepository.save(recommendation);
                System.out.println("Saved recommendation ID: " + r.getId());
                saved.add(r);

            } catch (Exception e) {
                System.err.println("Failed to save: " + rec.getTitle() + " — " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("Total saved: " + saved.size());
        return toResponseList(saved);
    }

    // Store user feedback on a recommendation
    public void saveFeedback(Long recommendationId, String email, boolean liked) {
        Recommendation recommendation = recommendationRepository
                .findById(recommendationId)
                .orElseThrow(() -> new RuntimeException("Recommendation not found"));

        if (!recommendation.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        String feedback = liked ? "[LIKED] " : "[DISLIKED] ";
        recommendation.setReasoning(feedback + recommendation.getReasoning());
        recommendationRepository.save(recommendation);
    }

    // Get all saved recommendations for a user
    public List<BookRecommendationDto.RecommendationResponse> getUserRecommendations(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Recommendation> recs = recommendationRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId());
        return toResponseList(recs);
    }

    // Map entities to clean response DTOs — never expose raw entities from the API
    private List<BookRecommendationDto.RecommendationResponse> toResponseList(List<Recommendation> recs) {
        return recs.stream().map(r -> {
            BookRecommendationDto.RecommendationResponse dto =
                    new BookRecommendationDto.RecommendationResponse();
            dto.setId(r.getId());
            dto.setBookTitle(r.getBook().getTitle());
            dto.setBookAuthor(r.getBook().getAuthor());
            dto.setReasoning(r.getReasoning());
            dto.setEngine(r.getEngine());
            dto.setCreatedAt(r.getCreatedAt());
            return dto;
        }).toList();
    }
}