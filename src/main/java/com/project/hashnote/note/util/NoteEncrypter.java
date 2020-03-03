package com.project.hashnote.note.util;

import com.project.hashnote.note.dto.EncryptionDetails;

public interface NoteEncrypter { // TODO: 09.01.2020 wyjebac moze interfejs bo niepotrzebny + łamie LSP
    EncryptionDetails encrypt(EncryptionDetails request);
    byte[] decrypt(EncryptionDetails encryptionDetails);
}
