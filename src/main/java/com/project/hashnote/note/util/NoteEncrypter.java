package com.project.hashnote.note.util;

import com.project.hashnote.note.dto.NoteRequest;
import com.project.hashnote.note.dto.EncryptionDetails;

public interface NoteEncrypter {
    EncryptionDetails encrypt(NoteRequest request);
    byte[] decrypt(EncryptionDetails encryptionDetails);
}
