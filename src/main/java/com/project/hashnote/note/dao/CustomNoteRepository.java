package com.project.hashnote.note.dao;

import com.project.hashnote.note.document.Note;

import java.util.List;

public interface CustomNoteRepository {
    List<Note> findAllLimitedNotes();
}
