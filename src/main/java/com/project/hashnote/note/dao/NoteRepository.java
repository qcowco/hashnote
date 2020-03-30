package com.project.hashnote.note.dao;

import com.project.hashnote.note.document.Note;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NoteRepository extends MongoRepository<Note, String> {
    void deleteById(String id);
    List<Note> findByAuthor(String username);
    List<Note> findByExpiresAtBefore(LocalDateTime now);
    @Query("{$where: \"this.maxVisits > 0 && this.keyVisits >= this.maxVisits\"}")
    List<Note> findAllLimitedNotes();
    Optional<Note> findByAuthorAndId(String username, String id);
    boolean existsById(String id);
}
