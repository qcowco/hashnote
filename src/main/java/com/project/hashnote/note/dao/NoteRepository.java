package com.project.hashnote.note.dao;

import com.project.hashnote.note.document.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NoteRepository extends MongoRepository<Note, String> {
    void deleteById(String id);
}
