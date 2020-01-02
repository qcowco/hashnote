package com.project.hashnote.note.util;

import com.project.hashnote.note.dto.EncryptionDetails;

public interface NoteEncoder {
    EncryptionDetails encode(EncryptionDetails result);
    EncryptionDetails decode(EncryptionDetails encoded);
}
