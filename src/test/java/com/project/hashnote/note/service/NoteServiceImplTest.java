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

    @DisplayName("Given a note is being created")
    @Nested
    class NoteCreation {

        @DisplayName("When encrypted")
        @Nested
        class HasEncryption {

            @DisplayName("Then response has: ID, KEY")
            @Test
            public void shouldReturnEncryptionResponseWithKey() {
                //Given
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

    @DisplayName("Given user is patching an unencrypted note")
    @Nested
    class NotePatching {

        @DisplayName("When couldn't find note by this user with given id")
        @Nested
        class NotAuthor {

            @DisplayName("Then throws ResourceNotFoundException")
            @Test
            public void shouldThrowResourceNotFoundException() {
                //Given
                when(noteRepository.findByAuthorAndId(USER, ID))
                        .thenReturn(Optional.empty());

                //When/Then
                assertThrows(ResourceNotFoundException.class, () -> noteService.patch(new NoteRequest(), USER, ID));
            }
        }

        @DisplayName("When requested note is actually encrypted")
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

        @DisplayName("When patch contains encryption method")
        @Nested
        class EncryptingPatch {

            @DisplayName("Then response has: ID, KEY")
            @Test
            public void shouldReturnEncryptionResultWithKey() {
                //Given
                Note note = new Note();
                note.setId(ID);

                NoteRequest patchRequest = new NoteRequest();
                patchRequest.setMethod(METHOD);

                NoteRequest noteRequest = new NoteRequest();
                noteRequest.setMethod(METHOD);

                EncryptionCredentials encryptionCredentials = new EncryptionCredentials();
                encryptionCredentials.setSecretKey(KEY.getBytes());

                when(noteRepository.findByAuthorAndId(USER, ID))
                        .thenReturn(Optional.of(note));

                when(noteMapper.noteToRequest(note))
                        .thenReturn(noteRequest);

                doNothing().when(noteMapper)
                        .copyProperties(patchRequest, noteRequest);

                when(noteEncrypter.encrypt(noteRequest))
                        .thenReturn(encryptionCredentials);

                doNothing().when(encryptionMapper)
                        .applyEncryption(encryptionCredentials, note);

                when(noteRepository.save(note))
                        .thenReturn(note);

                EncryptionResponse expectedResponse = new EncryptionResponse(ID, KEY);

                //When
                EncryptionResponse response = noteService.patch(patchRequest, USER, ID);

                //Then
                assertEquals(expectedResponse, response);
            }
        }


        @DisplayName("When patch doesn't contain an encryption method")
        @Nested
        class NormalPatch {

            @DisplayName("Then response has: ID")
            @Test
            public void shouldReturnEncryptionResultWithoutKey() {
                //Given
                Note note = new Note();
                note.setId(ID);

                NoteRequest patchRequest = new NoteRequest();

                NoteRequest noteRequest = new NoteRequest();

                when(noteRepository.findByAuthorAndId(USER, ID))
                        .thenReturn(Optional.of(note));

                when(noteMapper.noteToRequest(note))
                        .thenReturn(noteRequest);

                doNothing().when(noteMapper)
                        .copyProperties(patchRequest, noteRequest);

                when(noteRepository.save(note))
                        .thenReturn(note);

                EncryptionResponse expectedResponse = new EncryptionResponse(ID);

                //When
                EncryptionResponse response = noteService.patch(patchRequest, USER, ID);

                //Then
                assertEquals(expectedResponse, response);
            }
        }

    }

    @DisplayName("Given user is patching an encrypted note")
    @Nested
    class EncryptedNotePatching {

        @DisplayName("When couldn't find note by this user with given id")
        @Nested
        class NotAuthor {

            @DisplayName("Then throws ResourceNotFoundException")
            @Test
            public void shouldThrowResourceNotFoundException() {
                //Given
                when(noteRepository.findByAuthorAndId(USER, ID))
                        .thenReturn(Optional.empty());

                //When/Then
                assertThrows(ResourceNotFoundException.class, () -> noteService.patch(new NoteRequest(), USER, ID));
            }
        }

        @DisplayName("When key is good and request has an encryption method")
        @Nested
        class EncryptingPatch {

            @DisplayName("Then response has: ID, KEY")
            @Test
            public void shouldReturnEncryptionResultWithKey() {
                //Given
                Note note = new Note();
                note.setId(ID);

                NoteRequest patchRequest = new NoteRequest();
                patchRequest.setMethod(METHOD);

                NoteRequest noteRequest = new NoteRequest();
                noteRequest.setMethod(METHOD);

                EncryptionCredentials encryptionCredentials = new EncryptionCredentials();
                encryptionCredentials.setSecretKey(KEY.getBytes());

                when(noteRepository.findByAuthorAndId(USER, ID))
                        .thenReturn(Optional.of(note));

                when(noteMapper.noteToRequest(note))
                        .thenReturn(noteRequest);

                when(noteEncrypter.decrypt(note, KEY))
                        .thenReturn(MESSAGE.getBytes());

                doNothing().when(noteMapper)
                        .copyProperties(patchRequest, noteRequest);

                when(noteEncrypter.encrypt(noteRequest))
                        .thenReturn(encryptionCredentials);

                doNothing().when(encryptionMapper)
                        .applyEncryption(encryptionCredentials, note);

                when(noteRepository.save(note))
                        .thenReturn(note);

                EncryptionResponse expectedResponse = new EncryptionResponse(ID, KEY);

                //When
                EncryptionResponse response = noteService.patch(patchRequest, USER, ID, KEY);

                //Then
                assertEquals(expectedResponse, response);
            }
        }

        @DisplayName("When key is good and request has no encryption method")
        @Nested
        class NormalPatch {

            @DisplayName("Then response has: ID")
            @Test
            public void shouldThrowWrongKeyException() {
                //Given
                Note note = new Note();
                note.setId(ID);

                NoteRequest patchRequest = new NoteRequest();

                NoteRequest noteRequest = new NoteRequest();

                when(noteRepository.findByAuthorAndId(USER, ID))
                        .thenReturn(Optional.of(note));

                when(noteMapper.noteToRequest(note))
                        .thenReturn(noteRequest);

                when(noteEncrypter.decrypt(note, KEY))
                        .thenReturn(MESSAGE.getBytes());

                doNothing().when(noteMapper)
                        .copyProperties(patchRequest, noteRequest);

                when(noteRepository.save(note))
                        .thenReturn(note);

                EncryptionResponse expectedResponse = new EncryptionResponse(ID);

                //When
                EncryptionResponse response = noteService.patch(patchRequest, USER, ID, KEY);

                //Then
                assertEquals(expectedResponse, response);
            }
        }
    }

    @DisplayName("Given user is deleting a note")
    @Nested
    class UserNoteDeleting {

        @DisplayName("When couldn't find note by this user with given id")
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
}
