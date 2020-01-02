package com.project.hashnote.encoders;

import com.project.hashnote.encoders.exceptions.IncorrectPrivateKeyException;
import com.project.hashnote.note.dto.EncryptionDetails;

import javax.crypto.BadPaddingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;

public interface MessageEncoder {
    byte[] encode(byte[] message);
    byte[] decode(byte[] message) throws IncorrectPrivateKeyException;
    byte[] getSecretKey();
    byte[] getInitVector();
    String getMethod();
}
