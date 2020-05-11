package com.project.hashnote.note.util;

import com.project.hashnote.note.dto.EncryptionCredentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.apache.tomcat.util.codec.binary.Base64.encodeBase64;
import static org.apache.tomcat.util.codec.binary.Base64.decodeBase64;

import static org.junit.jupiter.api.Assertions.*;

class Base64EncoderTest {
    private final Base64Encoder base64Encoder = new Base64Encoder();
    private final byte[] MESSAGE = "MESSAGE".getBytes();
    private final byte[] KEY = "KEY".getBytes();
    private final byte[] VECTOR = "VECTOR".getBytes();

    private final String METHOD = "METHOD";

    private EncryptionCredentials credentials;

    @BeforeEach
    void initCredentials() {
        credentials = new EncryptionCredentials();
        credentials.setMessage(MESSAGE);
        credentials.setSecretKey(KEY);
        credentials.setVector(VECTOR);
        credentials.setMethod(METHOD);
    }

    @DisplayName("Returns doubly base64 encoded EncryptionCredentials")
    @Test
    void shouldReturnEncodedEncryptionDetails() {
        //Given
        EncryptionCredentials encodedCredentials = encode(credentials);

        //When
        EncryptionCredentials response = base64Encoder.encode(credentials);

        //Then
        assertEquals(encodedCredentials, response);
    }

    private EncryptionCredentials encode(EncryptionCredentials credentials) {
        EncryptionCredentials encodedCredentials = new EncryptionCredentials();

        encodedCredentials.setMessage(encodeBase64(encodeBase64(credentials.getMessage())));
        encodedCredentials.setSecretKey(encodeBase64(encodeBase64(credentials.getSecretKey())));
        encodedCredentials.setVector(encodeBase64(encodeBase64(credentials.getVector())));
        encodedCredentials.setMethod(credentials.getMethod());

        return encodedCredentials;
    }

    @DisplayName("Returns doubly base64 decoded EncryptionCredentials")
    @Test
    void shouldReturnDecodedEncryptionDetails() {
        //Given
        EncryptionCredentials encodedCredentials = encode(credentials);

        //When
        EncryptionCredentials response = base64Encoder.decode(encodedCredentials);

        //Then
        assertEquals(credentials, response);
    }

    private EncryptionCredentials decode(EncryptionCredentials credentials) {
        EncryptionCredentials encodedCredentials = new EncryptionCredentials();

        encodedCredentials.setMessage(decodeBase64(decodeBase64(credentials.getMessage())));
        encodedCredentials.setSecretKey(decodeBase64(decodeBase64(credentials.getSecretKey())));
        encodedCredentials.setVector(decodeBase64(decodeBase64(credentials.getVector())));
        encodedCredentials.setMethod(credentials.getMethod());

        return encodedCredentials;
    }

}
