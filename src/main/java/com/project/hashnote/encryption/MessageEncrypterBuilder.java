package com.project.hashnote.encryption;

import com.project.hashnote.encryption.algorithms.AlgorithmDetails;
import com.project.hashnote.note.dto.EncryptionDetails;

public interface MessageEncrypterBuilder {
    MessageEncrypterBuilder algorithmDetails(AlgorithmDetails algorithmDetails);
    MessageEncrypterBuilder encryptionDetails(EncryptionDetails encryptionDetails);
    MessageEncrypter build();
}
