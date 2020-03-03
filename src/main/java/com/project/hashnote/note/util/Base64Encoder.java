package com.project.hashnote.note.util;

import com.project.hashnote.note.dto.EncryptionDetails;
import org.springframework.stereotype.Component;

import static org.apache.tomcat.util.codec.binary.Base64.encodeBase64;
import static org.apache.tomcat.util.codec.binary.Base64.decodeBase64;

@Component
public class Base64Encoder implements NoteEncoder {
    @Override
    public EncryptionDetails encode(EncryptionDetails encryptionDetails) {
        EncryptionDetails encodedResult = new EncryptionDetails();

        encodedResult.setMessage(encodeBase64(encodeBase64(encryptionDetails.getMessage())));
        encodedResult.setSecretKey(encodeBase64(encodeBase64(encryptionDetails.getSecretKey())));
        encodedResult.setVector(encodeBase64(encodeBase64(encryptionDetails.getVector())));
        encodedResult.setMethod(encryptionDetails.getMethod());

        return encodedResult;
    }

    @Override
    public EncryptionDetails decode(EncryptionDetails encodedResult) {
        EncryptionDetails decodedResult = new EncryptionDetails();

        decodedResult.setMessage(decodeBase64(decodeBase64(encodedResult.getMessage())));
        decodedResult.setSecretKey(decodeBase64(decodeBase64(encodedResult.getSecretKey())));
        decodedResult.setVector(decodeBase64(decodeBase64(encodedResult.getVector())));
        decodedResult.setMethod(encodedResult.getMethod());

        return decodedResult;
    }


}
