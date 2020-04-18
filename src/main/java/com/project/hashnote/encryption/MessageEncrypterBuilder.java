package com.project.hashnote.encryption;

import com.project.hashnote.encryption.algorithms.AlgorithmDetails;
import com.project.hashnote.note.dto.EncryptionCredentials;

public interface MessageEncrypterBuilder {
    MessageEncrypterBuilder algorithmDetails(AlgorithmDetails algorithmDetails);
    MessageEncrypterBuilder encryptionCredentials(EncryptionCredentials encryptionCredentials);
    MessageEncrypter build();
}
