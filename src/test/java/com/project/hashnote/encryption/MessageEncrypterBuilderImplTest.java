package com.project.hashnote.encryption;

import com.project.hashnote.encryption.algorithms.AlgorithmDetails;
import com.project.hashnote.encryption.exceptions.MalformedPrivateKeyException;
import com.project.hashnote.encryption.exceptions.MalformedVectorException;
import com.project.hashnote.note.dto.EncryptionCredentials;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class MessageEncrypterBuilderImplTest {
    private final byte[] KEY = "KEY".getBytes();
    private final byte[] MESSAGE = "MESSAGE".getBytes();
    private final byte[] VECTOR = "VECTOR".getBytes();
    private final String METHOD = "METHOD";

    @DisplayName("Given a MessageEncrypter is being built")
    @Nested
    class BuildFinish {

        @DisplayName("When custom key has incorrect length")
        @Nested
        class KeyLength {

            @DisplayName("Then throws MalformedPrivateKeyException")
            @Test
            public void shouldThrowMalformedPrivateKeyException() {
                //Given
                AlgorithmDetails algorithmDetails = Mockito.mock(AlgorithmDetails.class);
                SecretKeySpec secretKey = Mockito.mock(SecretKeySpec.class);

                EncryptionCredentials credentialsInvalidKey = new EncryptionCredentials();
                credentialsInvalidKey.setMessage(MESSAGE);
                credentialsInvalidKey.setSecretKey(KEY);

                MessageEncrypterBuilderImpl encrypterBuilder = new MessageEncrypterBuilderImpl();
                encrypterBuilder.algorithmDetails(algorithmDetails);
                encrypterBuilder.encryptionCredentials(credentialsInvalidKey);

                when(secretKey.getEncoded())
                        .thenReturn(KEY);

                when(algorithmDetails.customSecretKey(KEY))
                        .thenReturn(secretKey);

                when(algorithmDetails.isKeyProperLength(KEY))
                        .thenReturn(false);

                //When/Then
                assertThrows(MalformedPrivateKeyException.class, encrypterBuilder::build);
            }
        }

        @DisplayName("When custom vector has incorrect length")
        @Nested
        class VectorLength {

            @DisplayName("Then throws MalformedVectorException")
            @Test
            public void shouldThrowMalformedVectorException() {
                //Given
                AlgorithmDetails algorithmDetails = Mockito.mock(AlgorithmDetails.class);
                SecretKeySpec secretKey = Mockito.mock(SecretKeySpec.class);

                EncryptionCredentials credentialsInvalidKey = new EncryptionCredentials();
                credentialsInvalidKey.setMessage(MESSAGE);
                credentialsInvalidKey.setVector(VECTOR);

                MessageEncrypterBuilderImpl encrypterBuilder = new MessageEncrypterBuilderImpl();
                encrypterBuilder.algorithmDetails(algorithmDetails);
                encrypterBuilder.encryptionCredentials(credentialsInvalidKey);

                when(algorithmDetails.randomSecretKey())
                        .thenReturn(secretKey);

                when(algorithmDetails.isVectorProperLength(VECTOR))
                        .thenReturn(false);

                //When/Then
                assertThrows(MalformedVectorException.class, encrypterBuilder::build);
            }
        }

        @DisplayName("When building with custom parameters")
        @Nested
        class CustomEncrypter {

            @DisplayName("Then returns MessageEncrypter with custom parameters")
            @Test
            public void shouldReturnCustomMessageEncrypter() {
                //Given
                AlgorithmDetails algorithmDetails = Mockito.mock(AlgorithmDetails.class);
                SecretKeySpec secretKey = Mockito.mock(SecretKeySpec.class);
                IvParameterSpec specVector = Mockito.mock(IvParameterSpec.class);
                Cipher cipher = Mockito.mock(Cipher.class);

                EncryptionCredentials encryptionCredentials = new EncryptionCredentials();
                encryptionCredentials.setMessage(MESSAGE);
                encryptionCredentials.setSecretKey(KEY);
                encryptionCredentials.setVector(VECTOR);

                MessageEncrypterBuilderImpl encrypterBuilder = new MessageEncrypterBuilderImpl();
                encrypterBuilder.algorithmDetails(algorithmDetails);
                encrypterBuilder.encryptionCredentials(encryptionCredentials);

                when(secretKey.getEncoded())
                        .thenReturn(KEY);

                when(specVector.getIV())
                        .thenReturn(VECTOR);

                when(algorithmDetails.customSecretKey(KEY))
                        .thenReturn(secretKey);

                when(algorithmDetails.isKeyProperLength(KEY))
                        .thenReturn(true);

                when(algorithmDetails.customInitVector(VECTOR))
                        .thenReturn(specVector);

                when(algorithmDetails.isVectorProperLength(VECTOR))
                        .thenReturn(true);

                when(algorithmDetails.getCipher())
                        .thenReturn(cipher);

                //When
                MessageEncrypter messageEncrypter = encrypterBuilder.build();

                //Then
                assertEquals(encryptionCredentials, messageEncrypter.getEncryptionCredentials());
            }
        }

        @DisplayName("When building with randomized parameters")
        @Nested
        class RandomizedEncrypter {

            @DisplayName("Then returns MessageEncrypter with random parameters")
            @Test
            public void shouldReturnRandomizedMessageEncrypter() {
                //Given
                AlgorithmDetails algorithmDetails = Mockito.mock(AlgorithmDetails.class);
                SecretKeySpec secretKey = Mockito.mock(SecretKeySpec.class);
                IvParameterSpec specVector = Mockito.mock(IvParameterSpec.class);
                Cipher cipher = Mockito.mock(Cipher.class);

                EncryptionCredentials encryptionCredentials = new EncryptionCredentials();
                encryptionCredentials.setMessage(MESSAGE);

                MessageEncrypterBuilderImpl encrypterBuilder = new MessageEncrypterBuilderImpl();
                encrypterBuilder.algorithmDetails(algorithmDetails);
                encrypterBuilder.encryptionCredentials(encryptionCredentials);

                EncryptionCredentials expectedCredentials = new EncryptionCredentials();
                expectedCredentials.setMessage(MESSAGE);
                expectedCredentials.setSecretKey(KEY);
                expectedCredentials.setVector(VECTOR);

                when(secretKey.getEncoded())
                        .thenReturn(KEY);

                when(specVector.getIV())
                        .thenReturn(VECTOR);

                when(algorithmDetails.randomSecretKey())
                        .thenReturn(secretKey);

                when(algorithmDetails.randomInitVector())
                        .thenReturn(specVector);

                when(algorithmDetails.getCipher())
                        .thenReturn(cipher);

                //When
                MessageEncrypter messageEncrypter = encrypterBuilder.build();

                //Then
                assertEquals(expectedCredentials, messageEncrypter.getEncryptionCredentials());
            }
        }

    }
}
