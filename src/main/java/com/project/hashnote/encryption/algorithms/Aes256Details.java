package com.project.hashnote.encryption.algorithms;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
public @Data class Aes256Details extends AlgorithmDetails {
    private final String method = "AES";
    private final String mode = "CBC";
    private final String padding = "PKCS5Padding";
    private final int keySize = 256;
    private final int vectorSize = 128;

    @Override
    int getKeyLength() {
        return keySize/8;
    }
}
// TODO: 28.12.2019 add - triple DES, Blowfish, Twofish[, RSA?]
