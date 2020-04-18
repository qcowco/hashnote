package com.project.hashnote.note.util;

import com.project.hashnote.note.dto.EncryptionCredentials;

public interface NoteEncoder {
    EncryptionCredentials encode(EncryptionCredentials result);
    EncryptionCredentials decode(EncryptionCredentials encoded);
}
