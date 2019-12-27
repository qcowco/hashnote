package com.project.hashnote.encoders;

import com.project.hashnote.dao.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
public class EncoderUtil {
    NoteRepository noteRepository;

    @Autowired
    public EncoderUtil(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public boolean verify(byte[] encodedMessage, byte[] key) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        AesEncryptor encryptor = AesEncryptor.builder()
                .secretKey(key)
                .build();

        boolean isKeyValid = true;

        try {
            encryptor.decode(encodedMessage);
        } catch (InvalidKeyException e) {
            isKeyValid = false;
        }

        return isKeyValid;
    }

}
