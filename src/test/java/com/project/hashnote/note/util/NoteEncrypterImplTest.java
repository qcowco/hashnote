package com.project.hashnote.note.util;

import com.project.hashnote.encryption.MessageEncrypter;
import com.project.hashnote.encryption.MessageEncrypterBuilder;
import com.project.hashnote.encryption.algorithms.AlgorithmDetails;
import com.project.hashnote.encryption.exceptions.InvalidAlgorithmNameException;
import com.project.hashnote.note.document.Note;
import com.project.hashnote.note.dto.EncryptionCredentials;
import com.project.hashnote.note.dto.NoteRequest;
import com.project.hashnote.note.mapper.EncryptionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteEncrypterImplTest {
    private final String ALGORITHM = "ALGORITHM";
    private final String KEY = "KEY";
    private final byte[] MESSAGE = "MESSAGE".getBytes();

    private List<AlgorithmDetails> algorithms;
    private MessageEncrypterBuilder encrypterBuilder;
    private EncryptionMapper encryptionMapper;
    private NoteEncoder noteEncoder;
    private NoteEncrypter noteEncrypter;

    private EncryptionCredentials encryptionCredentials;
    private AlgorithmDetails algorithmDetails;

    @BeforeEach
    public void setup() {
        algorithms = new LinkedList<>();

        algorithmDetails = Mockito.mock(AlgorithmDetails.class);
        algorithms.add(algorithmDetails);

        encrypterBuilder = Mockito.mock(MessageEncrypterBuilder.class);

        encryptionMapper = Mockito.mock(EncryptionMapper.class);

        noteEncoder = Mockito.mock(NoteEncoder.class);

        noteEncrypter = new NoteEncrypterImpl(algorithms, encrypterBuilder, encryptionMapper, noteEncoder);

        encryptionCredentials = new EncryptionCredentials();
        encryptionCredentials.setMethod(ALGORITHM);
    }

    @DisplayName("Given a NoteRequest is being encrypted")
    @Nested
    class EncryptingRequest {

        @DisplayName("When requested algorithm isn't found")
        @Nested
        class AlgorithmNotFound {

            @DisplayName("Then should throw InvalidAlgorithmNameException")
            @Test
            public void shouldThrowInvalidAlgorithmNameException() {
                //Given
                NoteRequest noteRequest = new NoteRequest();

                when(encryptionMapper.getEncryptionDetails(noteRequest))
                        .thenReturn(encryptionCredentials);

                when(algorithmDetails.isMethod(ALGORITHM))
                        .thenReturn(false);

                //When/Then
                assertThrows(InvalidAlgorithmNameException.class, () -> noteEncrypter.encrypt(noteRequest));
            }
        }

        @DisplayName("When requested algorithm is found")
        @Nested
        class AlgorithmFound {

            @DisplayName("Then encrypts request")
            @Test
            public void shouldThrowInvalidAlgorithmNameException() {
                //Given
                NoteRequest noteRequest = new NoteRequest();

                MessageEncrypter messageEncrypter = Mockito.mock(MessageEncrypter.class);

                EncryptionCredentials encryptionResult = new EncryptionCredentials();

                EncryptionCredentials encodedResult = new EncryptionCredentials();

                when(encryptionMapper.getEncryptionDetails(noteRequest))
                        .thenReturn(encryptionCredentials);

                when(algorithmDetails.isMethod(ALGORITHM))
                        .thenReturn(true);

                when(encrypterBuilder.algorithmDetails(algorithmDetails))
                        .thenReturn(encrypterBuilder);

                when(encrypterBuilder.encryptionCredentials(encryptionCredentials))
                        .thenReturn(encrypterBuilder);

                when(encrypterBuilder.build())
                        .thenReturn(messageEncrypter);

                when(messageEncrypter.getEncryptionCredentials())
                        .thenReturn(encryptionResult);

                when(noteEncoder.encode(encryptionResult))
                        .thenReturn(encodedResult);

                //When/Then
                assertDoesNotThrow(() -> noteEncrypter.encrypt(noteRequest));
            }
        }
    }

    @DisplayName("Given a Note is being decrypted")
    @Nested
    class DecryptingNote {

        @DisplayName("When requested algorithm isn't found")
        @Nested
        class AlgorithmNotFound {

            @DisplayName("Then should throw InvalidAlgorithmNameException")
            @Test
            public void shouldThrowInvalidAlgorithmNameException() {
                //Given
                Note note = new Note();

                AlgorithmDetails algorithmDetails = Mockito.mock(AlgorithmDetails.class);
                algorithms.add(algorithmDetails);

                when(encryptionMapper.noteAndKeyToEncryption(note, KEY))
                        .thenReturn(encryptionCredentials);

                when(noteEncoder.decode(encryptionCredentials))
                        .thenReturn(encryptionCredentials);

                when(algorithmDetails.isMethod(ALGORITHM))
                        .thenReturn(false);

                //When/Then
                assertThrows(InvalidAlgorithmNameException.class, () -> noteEncrypter.decrypt(note, KEY));
            }
        }

        @DisplayName("When requested algorithm is found")
        @Nested
        class AlgorithmFound {

            @DisplayName("Then returns decrypted message")
            @Test
            public void shouldReturnDecryptedMessage() {
                //Given
                Note note = new Note();

                MessageEncrypter messageEncrypter = Mockito.mock(MessageEncrypter.class);

                EncryptionCredentials resultCredentials = new EncryptionCredentials();
                resultCredentials.setMessage(MESSAGE);

                when(encryptionMapper.noteAndKeyToEncryption(note, KEY))
                        .thenReturn(encryptionCredentials);

                when(noteEncoder.decode(encryptionCredentials))
                        .thenReturn(encryptionCredentials);

                when(algorithmDetails.isMethod(ALGORITHM))
                        .thenReturn(true);

                when(encrypterBuilder.algorithmDetails(algorithmDetails))
                        .thenReturn(encrypterBuilder);

                when(encrypterBuilder.encryptionCredentials(encryptionCredentials))
                        .thenReturn(encrypterBuilder);

                when(encrypterBuilder.build())
                        .thenReturn(messageEncrypter);

                when(messageEncrypter.getEncryptionCredentials())
                        .thenReturn(resultCredentials);

                //When/Then
                assertEquals(noteEncrypter.decrypt(note, KEY).getClass(), byte[].class);
            }
        }
    }

}
