package com.project.hashnote.note.util;

import com.project.hashnote.note.dto.EncryptionDetails;

public interface NoteEncrypter {
    EncryptionDetails encrypt(EncryptionDetails request);
    byte[] decrypt(EncryptionDetails encryptionDetails);
}
