package com.project.hashnote.encryption.algorithms;

import org.springframework.stereotype.Component;

@Component
public class Aes256Details implements AlgorithmDetails {
    private String method = "AES";
    private String mode = "CBC";
    private String padding = "PKCS5Padding";
    private int keySize = 256;
    private int vectorSize = 128;

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getMode() {
        return mode;
    }

    @Override
    public String getPadding() {
        return padding;
    }

    @Override
    public int getKeySize() {
        return keySize;
    }

    @Override
    public int getKeyByteSize() {
        return keySize/8;
    }

    @Override
    public int getVectorSize() {
        return vectorSize;
    }

    @Override
    public int getVectorByteSize() {
        return vectorSize/8;
    }
}
// TODO: 28.12.2019 add - triple DES, Blowfish, Twofish[, RSA?]