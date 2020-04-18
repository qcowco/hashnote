package com.project.hashnote.note.service;

import com.project.hashnote.exceptions.ResourceNotFoundException;
import com.project.hashnote.note.dao.NoteRepository;
import com.project.hashnote.note.document.EncryptionDetails;
import com.project.hashnote.note.document.Note;
import com.project.hashnote.note.dto.EncryptionCredentials;
import com.project.hashnote.note.dto.EncryptionResponse;
import com.project.hashnote.note.dto.NoteDto;
import com.project.hashnote.note.dto.NoteRequest;
import com.project.hashnote.note.exception.NoteExpiredException;
import com.project.hashnote.note.exception.UnlockLimitExceededException;
import com.project.hashnote.note.mapper.EncryptionMapper;
import com.project.hashnote.note.mapper.NoteMapper;
import com.project.hashnote.note.util.NoteEncrypter;
import com.project.hashnote.security.exception.UnauthorizedAccessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteServiceImplTest {
    private final String ID = "ID";
    private final String KEY = "KEY";
    private final String METHOD = "METHOD";
    private final String USER = "USER";
    private final String MESSAGE = "MESSAGE";

    @InjectMocks
    private NoteServiceImpl noteService;

    @Mock
    private NoteEncrypter noteEncrypter;

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private NoteMapper noteMapper;

    @Mock
    private EncryptionMapper encryptionMapper;

    //given a note is being created
    @DisplayName("Given a note is being created")
    @Nested
    class NoteCreation {

        //when encryption method parameter is given
        @DisplayName("When encrypted")
        @Nested
        class HasEncryption {

            @DisplayName("Then response has: ID, KEY")
            @Test
            public void shouldReturnEncryptionResponseWithKey() {
                //Give
                NoteRequest noteRequest = new NoteRequest();
                noteRequest.setMethod(METHOD);

                Note note = new Note();
                note.setId(ID);

                EncryptionCredentials encryptionCredentials = new EncryptionCredentials();
                encryptionCredentials.setSecretKey(KEY.getBytes());

                EncryptionResponse expectedResponse = new EncryptionResponse(ID, KEY);

                when(noteMapper.requestToNote(noteRequest, USER))
                        .thenReturn(note);

                when(noteEncrypter.encrypt(noteRequest))
                        .thenReturn(encryptionCredentials);

                doNothing()
                        .when(encryptionMapper).applyEncryption(encryptionCredentials, note);

                when(noteRepository.save(note))
                        .thenReturn(note);

                //When
                EncryptionResponse response = noteService.save(noteRequest, USER);

                //Then
                assertEquals(expectedResponse, response);
            }
        }

        //when no method parameter is given
        @DisplayName("When not encrypted")
        @Nested
        class NoEncryption {

            @DisplayName("Then response has: ID")
            @Test
            public void shouldReturnEncryptionResultWithoutKey() {
                //Given
                NoteRequest noteRequest = new NoteRequest();

                Note note = new Note();
                note.setId(ID);

                EncryptionCredentials encryptionCredentials = new EncryptionCredentials();
                encryptionCredentials.setSecretKey(KEY.getBytes());

                EncryptionResponse expectedResponse = new EncryptionResponse(ID);

                when(noteMapper.requestToNote(noteRequest, USER))
                        .thenReturn(note);

                when(noteRepository.save(note))
                        .thenReturn(note);

                //When
                EncryptionResponse response = noteService.save(noteRequest, USER);

                //Then
                assertEquals(expectedResponse, response);
            }
        }

    }

    @DisplayName("Given user's notes are being fetched")
    @Nested
    class GetAllNotes {

        @DisplayName("When a username is given")
        @Nested
        class UserLoggedIn {
            private final String USERNAME = "USER";

            @DisplayName("Then returns all his notes")
            @Test
            public void shouldReturnAllUsersNotes() {
                //Given
                List<Note> notes = new ArrayList<>();
                notes.add(new Note());
                notes.add(new Note());

                when(noteRepository.findByAuthor(USERNAME))
                        .thenReturn(notes);

                List<NoteDto> expectedNoteDtos = new ArrayList<>();
                expectedNoteDtos.add(new NoteDto());
                expectedNoteDtos.add(new NoteDto());

                when(noteMapper.noteToNoteDtoList(notes))
                        .thenReturn(expectedNoteDtos);

                //When
                List<NoteDto> response = noteService.getAllBy(USERNAME);

                //Then
                assertEquals(expectedNoteDtos, response);
            }
        }
    }

    @DisplayName("Given a note is being fetched")
    @Nested
    class GetOne {

        @DisplayName("When it exists")
        @Nested
        class NoteExists {

            @DisplayName("Then returns it")
            @Test
            public void shouldReturnNote() {
                //Given
                Note note = new Note();
                NoteDto expectedDto = new NoteDto();

                when(noteRepository.findById(ID))
                        .thenReturn(Optional.of(note));

                when(noteMapper.noteToNoteDto(note))
                        .thenReturn(expectedDto);

                //When
                NoteDto response = noteService.getOne(ID);

                //Then
                assertEquals(expectedDto, response);
            }
        }

        @DisplayName("When it doesn't exist")
        @Nested
        class NoteNotFound {

            @DisplayName("Then throws ResourceNotFoundException")
            @Test
            public void shouldThrowResourceNotFoundException() {
                //Given
                when(noteRepository.findById(ID))
                        .thenReturn(Optional.empty());

                //When/Then
                assertThrows(ResourceNotFoundException.class, () -> noteService.getOne(ID));
            }
        }
    }

    @DisplayName("Given a note is being decrypted")
    @Nested
    class NoteDecrypting {

        @DisplayName("When the unlock limit has been reached")
        @Nested
        class NoteLimited {

            @DisplayName("Then throws UnlockLimitExceededException")
            @Test
            public void shouldThrowUnlockLimitExceededException() {
                //Given
                Note note = new Note();
                note.setExpiresAt(LocalDateTime.of(9999, 10, 10, 10, 10));

                note.setKeyVisits(2);
                note.setMaxVisits(1);

                when(noteRepository.findById(ID))
                        .thenReturn(Optional.of(note));

                //When/Then
                assertThrows(UnlockLimitExceededException.class, () -> noteService.getDecrypted(ID, KEY));
            }
        }

        @DisplayName("When the note is expired")
        @Nested
        class NoteExpired {

            @DisplayName("Then throws NoteExpiredException")
            @Test
            public void shouldThrowNoteExpiredException() {
                //Given
                Note note = new Note();
                note.setExpiresAt(LocalDateTime.of(2010, 10, 10, 10, 10));

                when(noteRepository.findById(ID))
                        .thenReturn(Optional.of(note));

                //When/Then
                assertThrows(NoteExpiredException.class, () -> noteService.getDecrypted(ID, KEY));
            }
        }

        @DisplayName("When decryption is successful")
        @Nested
        class NoteDecrypted {

            @DisplayName("Then returns a note")
            @Test
            public void shouldReturnNote() {
                //Given
                Note note = new Note();
                note.setExpiresAt(LocalDateTime.of(9999, 10, 10, 10, 10));

                when(noteRepository.findById(ID))
                        .thenReturn(Optional.of(note));

                when(noteEncrypter.decrypt(note, KEY))
                        .thenReturn(MESSAGE.getBytes());

                when(noteRepository.save(note))
                        .thenReturn(note);

                NoteDto expectedNoteDto = new NoteDto();
                expectedNoteDto.setMessage(MESSAGE);

                when(noteMapper.noteAndMessageToNoteDto(note, MESSAGE.getBytes()))
                        .thenReturn(expectedNoteDto);

                //When
                NoteDto response = noteService.getDecrypted(ID, KEY);

                //Then
                assertEquals(expectedNoteDto, response);
            }
        }

    }

    @DisplayName("Given user is patching a note")
    @Nested
    class NotePatching {

        @DisplayName("When requested note is encrypted")
        @Nested
        class NoteIsEncrypted {

            @DisplayName("Then throws UnauthorizedAccessException")
            @Test
            public void shouldThrowUnauthorizedAccessException() {
                //Given
                EncryptionDetails encryptionDetails = new EncryptionDetails();
                encryptionDetails.setMethod(METHOD);

                Note note = new Note();
                note.setEncryptionDetails(encryptionDetails);

                when(noteRepository.findByAuthorAndId(USER, ID))
                        .thenReturn(Optional.of(note));

                //When/Then
                assertThrows(UnauthorizedAccessException.class, () -> noteService.patch(new NoteRequest(), USER, ID));
            }
        }
//        class
    }

    @DisplayName("Given user is patching an encrypted note")
    @Nested
    class EncryptedNotePatching {

//        class
    }

    @DisplayName("Given user is deleting a note")
    @Nested
    class UserNoteDeleting {

        @DisplayName("When user isn't author")
        @Nested
        class NoteExists {

            @DisplayName("Then throws ResourceNotFoundException")
            @Test
            public void shouldDeleteNote() {
                when(noteRepository.findByAuthorAndId(USER, ID))
                        .thenReturn(Optional.empty());

                assertThrows(ResourceNotFoundException.class, () -> noteService.delete(ID, USER));
            }
        }
    }

//    @DisplayName("Given a note is being deleted")
//    @Nested
//    class NoteDeleting {
//
//        @DisplayName("When note exists")
//        @Nested
//        class NoteExists {
//
//            @DisplayName("Then it's deleted")
//            @Test
//            public void shouldDeleteNote() {
//
//            }
//        }
//    }
}
