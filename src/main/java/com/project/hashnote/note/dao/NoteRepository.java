package com.project.hashnote.note.dao;

import com.project.hashnote.note.document.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NoteRepository extends MongoRepository<Note, String> {
    void deleteById(String id);
    List<Note> findByAuthor(String username);
}
