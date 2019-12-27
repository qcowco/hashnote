package com.project.hashnote.encoders.algorithms;

public interface AlgorithmDetails {
    String getMethod();
    String getMode();
    String getPadding();
    int getKeySize();
    int getVectorSize();
}
