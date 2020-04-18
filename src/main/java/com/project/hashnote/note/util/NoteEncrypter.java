package com.project.hashnote.note.util;

import com.project.hashnote.note.document.Note;
import com.project.hashnote.note.dto.EncryptionCredentials;
import com.project.hashnote.note.dto.NoteRequest;

public interface NoteEncrypter {
    EncryptionCredentials encrypt(NoteRequest noteRequest);
    byte[] decrypt(Note note, String secretKey);
}
