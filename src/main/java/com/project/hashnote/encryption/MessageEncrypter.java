package com.project.hashnote.encryption;

import com.project.hashnote.encryption.exceptions.IncorrectPrivateKeyException;
import com.project.hashnote.note.dto.EncryptionDetails;

public interface MessageEncrypter {
    void encrypt(byte[] message);
    void decrypt(byte[] message) throws IncorrectPrivateKeyException;
    EncryptionDetails getEncryptionDetails();
}
