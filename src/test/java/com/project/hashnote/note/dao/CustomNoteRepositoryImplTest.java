package com.project.hashnote.note.dao;

import com.project.hashnote.note.document.Note;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class CustomNoteRepositoryImplTest {
    private NoteRepository noteRepository;

    @Autowired
    public CustomNoteRepositoryImplTest(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @DisplayName("Given limited notes are being fetched")
    @Nested
    class GetLimited {

        @DisplayName("Then returns limited notes")
        @Test
        public void shouldReturnNotesWithExceededMaxVisits() {
            //Given
            Note freshNote = new Note();
            freshNote.setKeyVisits(0);
            freshNote.setMaxVisits(10);

            Note limitedNote = new Note();
            limitedNote.setMaxVisits(10);
            limitedNote.setKeyVisits(10);

            noteRepository.save(freshNote);
            noteRepository.save(limitedNote);

            //When
            List<Note> limitedNotes = Arrays.asList(limitedNote);
            List<Note> repositoryResponse = noteRepository.findAllLimitedNotes();

            //Then
            assertEquals(limitedNotes, repositoryResponse);
        }
    }
}
