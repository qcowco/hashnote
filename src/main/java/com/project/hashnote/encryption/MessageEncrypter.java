package com.project.hashnote.encryption;

import com.project.hashnote.encryption.exceptions.IncorrectPrivateKeyException;
import com.project.hashnote.note.dto.EncryptionCredentials;

public interface MessageEncrypter {
    void encrypt();
    void decrypt() throws IncorrectPrivateKeyException;
    EncryptionCredentials getEncryptionCredentials();
}
