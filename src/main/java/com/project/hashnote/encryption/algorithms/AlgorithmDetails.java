package com.project.hashnote.encryption.algorithms;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public interface AlgorithmDetails {
    boolean isMethod(String method);

    SecretKey randomSecretKey();
    SecretKey customSecretKey(byte[] customKey);
    boolean isKeyProperLength(byte[] key);

    IvParameterSpec randomInitVector();
    IvParameterSpec customInitVector(byte[] customVector);
    boolean isVectorProperLength(byte[] vector);

    Cipher getCipher();
}
