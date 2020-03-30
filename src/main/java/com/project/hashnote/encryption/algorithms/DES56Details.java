package com.project.hashnote.encryption.algorithms;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
public @Data class DES56Details extends AlgorithmDetails {
    private final String method = "DES";
    private final String mode = "CBC";
    private final String padding = "PKCS5Padding";
    private final int keySize = 56;
    private final int vectorSize = 64;

    @Override
    int getKeyLength() {
        return (keySize + 8)/8;
    }
}
