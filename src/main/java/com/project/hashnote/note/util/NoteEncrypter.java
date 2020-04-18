package com.project.hashnote.note.util;

import com.project.hashnote.note.document.Note;
import com.project.hashnote.note.dto.EncryptionDetails;
import com.project.hashnote.note.dto.NoteRequest;

public interface NoteEncrypter {
    EncryptionDetails encrypt(NoteRequest noteRequest);
    byte[] decrypt(Note note, String secretKey);
}
