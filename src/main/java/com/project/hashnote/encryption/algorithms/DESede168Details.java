package com.project.hashnote.encryption.algorithms;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
public @Data class DESede168Details extends AlgorithmDetails {
    private final String method = "DESede";
    private final String mode = "CBC";
    private final String padding = "PKCS5Padding";
    private final int keySize = 168;
    private final int vectorSize = 64;

    @Override
    int getKeyLength() {
        return (keySize + 24)/8;
    }
}
