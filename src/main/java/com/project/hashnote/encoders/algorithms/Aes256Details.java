package com.project.hashnote.encoders.algorithms;

public class Aes256Details implements AlgorithmDetails {
    private String method = "AES";
    private String mode = "CBC";
    private String padding = "PKCS5Padding";
    private int keySize = 256;
    private int vectorSize = 16;

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
    public int getVectorSize() {
        return vectorSize;
    }
}
