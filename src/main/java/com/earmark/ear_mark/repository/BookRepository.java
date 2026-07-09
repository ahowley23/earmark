package com.earmark.ear_mark.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.earmark.ear_mark.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Look up a book by its Open Library ID before saving
    // so we never create duplicate book entries
    Optional<Book> findByOpenLibraryId(String openLibraryId);

    // Find by title and author as a fallback when we don't have
    // an Open Library ID yet (Claude-generated recommendations)
    Optional<Book> findByTitleAndAuthor(String title, String author);
}