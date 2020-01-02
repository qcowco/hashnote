package com.project.hashnote.encryption;

import com.project.hashnote.encryption.exceptions.IncorrectPrivateKeyException;

public interface MessageEncrypter {
    byte[] encrypt(byte[] message);
    byte[] decrypt(byte[] message) throws IncorrectPrivateKeyException;
    byte[] getSecretKey();
    byte[] getInitVector();
    String getMethod();
}
