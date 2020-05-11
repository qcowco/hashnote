package com.project.hashnote.encryption;

import com.project.hashnote.encryption.algorithms.Aes256Details;
import com.project.hashnote.encryption.algorithms.AlgorithmDetails;
import com.project.hashnote.encryption.exceptions.IncorrectPrivateKeyException;
import com.project.hashnote.note.dto.EncryptionCredentials;
import org.bson.internal.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MessageEncrypterImplTest {
    private final byte[] DECRYPTED_MESSAGE = "MESSAGE".getBytes();
    private final byte[] ENCRYPTED_MESSAGE = Base64.decode("UPBudJa298mkPnHPuOTIBg==");

    private final byte[] GOOD_KEY = "U0VDUkVUX0FFUzI1Nl9LRVlfMTAxMDEw".getBytes();
    private final byte[] WRONG_KEY = "V1JPTkdfQUVTMjU2X0tFWV8xMDEwMTA=".getBytes();
    private final byte[] GOOD_VECTOR = "VkVDVE9SX0FFUw==".getBytes();
    private final byte[] WRONG_VECTOR = "V1JPTkdfQUVTX1Y=".getBytes();

    private final String ALGORITHM = "AES";

    private AlgorithmDetails algorithmDetails = new Aes256Details();

    @DisplayName("Given a message is being encrypted")
    @Nested
    class EncryptingMessage {

        @DisplayName("Then message gets encrypted")
        @Test
        public void shouldEncryptMessage() {
            //Given
            MessageEncrypterBuilder encrypterBuilder = new MessageEncrypterBuilderImpl();

            EncryptionCredentials encryptionCredentials = new EncryptionCredentials();
            encryptionCredentials.setMessage(DECRYPTED_MESSAGE);
            encryptionCredentials.setSecretKey(GOOD_KEY);
            encryptionCredentials.setVector(GOOD_VECTOR);

            encrypterBuilder.encryptionCredentials(encryptionCredentials);
            encrypterBuilder.algorithmDetails(algorithmDetails);

            MessageEncrypter messageEncrypter = encrypterBuilder.build();

            EncryptionCredentials expectedCredentials = new EncryptionCredentials();
            expectedCredentials.setMethod(ALGORITHM);
            expectedCredentials.setMessage(ENCRYPTED_MESSAGE);
            expectedCredentials.setVector(GOOD_VECTOR);
            expectedCredentials.setSecretKey(GOOD_KEY);

            //When
            messageEncrypter.encrypt();

            EncryptionCredentials actualCredentials = messageEncrypter.getEncryptionCredentials();

            //Then
            assertEquals(expectedCredentials, actualCredentials);
        }

    }

    @DisplayName("Given a message is being decrypted")
    @Nested
    class DecryptingMessage {

        @DisplayName("When secret key and init vector are correct")
        @Nested
        class CorrectDecrypt {

            @DisplayName("Then message gets decrypted")
            @Test
            public void shouldDecryptMessage() {
                //Given
                MessageEncrypterBuilder encrypterBuilder = new MessageEncrypterBuilderImpl();

                EncryptionCredentials encryptionCredentials = new EncryptionCredentials();
                encryptionCredentials.setMessage(ENCRYPTED_MESSAGE);
                encryptionCredentials.setSecretKey(GOOD_KEY);
                encryptionCredentials.setVector(GOOD_VECTOR);

                encrypterBuilder.encryptionCredentials(encryptionCredentials);
                encrypterBuilder.algorithmDetails(algorithmDetails);

                MessageEncrypter messageEncrypter = encrypterBuilder.build();

                EncryptionCredentials expectedCredentials = new EncryptionCredentials();
                expectedCredentials.setMethod(ALGORITHM);
                expectedCredentials.setMessage(DECRYPTED_MESSAGE);
                expectedCredentials.setVector(GOOD_VECTOR);
                expectedCredentials.setSecretKey(GOOD_KEY);

                //When
                messageEncrypter.decrypt();

                EncryptionCredentials actualCredentials = messageEncrypter.getEncryptionCredentials();

                //Then
                assertEquals(expectedCredentials, actualCredentials);
            }
        }

        @DisplayName("When provided secret key is incorrect")
        @Nested
        class IncorrectKey {

            @DisplayName("Then throws IncorrectPrivateKeyException")
            @Test
            public void shouldThrowIncorrectPrivateKeyException() {
                //Given
                MessageEncrypterBuilder encrypterBuilder = new MessageEncrypterBuilderImpl();

                EncryptionCredentials encryptionCredentials = new EncryptionCredentials();
                encryptionCredentials.setMessage(ENCRYPTED_MESSAGE);
                encryptionCredentials.setSecretKey(WRONG_KEY);
                encryptionCredentials.setVector(GOOD_VECTOR);

                encrypterBuilder.encryptionCredentials(encryptionCredentials);
                encrypterBuilder.algorithmDetails(algorithmDetails);

                MessageEncrypter messageEncrypter = encrypterBuilder.build();

                //THEN
                assertThrows(IncorrectPrivateKeyException.class, messageEncrypter::decrypt);
            }
        }

        @DisplayName("When provided initialization vector is incorrect")
        @Nested
        class IncorrectVector {

            @DisplayName("Then throws IncorrectPrivateKeyException")
            @Test
            public void shouldDecryptIncorrectly() {
                //Given
                MessageEncrypterBuilder encrypterBuilder = new MessageEncrypterBuilderImpl();

                EncryptionCredentials encryptionCredentials = new EncryptionCredentials();
                encryptionCredentials.setMessage(ENCRYPTED_MESSAGE);
                encryptionCredentials.setSecretKey(GOOD_KEY);
                encryptionCredentials.setVector(WRONG_VECTOR);

                encrypterBuilder.encryptionCredentials(encryptionCredentials);
                encrypterBuilder.algorithmDetails(algorithmDetails);

                MessageEncrypter messageEncrypter = encrypterBuilder.build();

                //Then
                assertThrows(IncorrectPrivateKeyException.class, messageEncrypter::decrypt);
            }
        }
    }

}
