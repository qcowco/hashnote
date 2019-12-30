package com.project.hashnote.dao;

import com.project.hashnote.document.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NoteRepository extends MongoRepository<Note, String> {
    void deleteById(String id);
}
