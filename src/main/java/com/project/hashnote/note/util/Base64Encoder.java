package com.project.hashnote.note.util;

import com.project.hashnote.note.dto.EncryptionCredentials;
import org.springframework.stereotype.Component;

import static org.apache.tomcat.util.codec.binary.Base64.encodeBase64;
import static org.apache.tomcat.util.codec.binary.Base64.decodeBase64;

@Component
public class Base64Encoder implements NoteEncoder {
    @Override
    public EncryptionCredentials encode(EncryptionCredentials encryptionCredentials) {
        EncryptionCredentials encodedResult = new EncryptionCredentials();

        encodedResult.setMessage(encodeBase64(encodeBase64(encryptionCredentials.getMessage())));
        encodedResult.setSecretKey(encodeBase64(encodeBase64(encryptionCredentials.getSecretKey())));
        encodedResult.setVector(encodeBase64(encodeBase64(encryptionCredentials.getVector())));
        encodedResult.setMethod(encryptionCredentials.getMethod());

        return encodedResult;
    }

    @Override
    public EncryptionCredentials decode(EncryptionCredentials encodedResult) {
        EncryptionCredentials decodedResult = new EncryptionCredentials();

        decodedResult.setMessage(decodeBase64(decodeBase64(encodedResult.getMessage())));
        decodedResult.setSecretKey(decodeBase64(decodeBase64(encodedResult.getSecretKey())));
        decodedResult.setVector(decodeBase64(decodeBase64(encodedResult.getVector())));
        decodedResult.setMethod(encodedResult.getMethod());

        return decodedResult;
    }


}
