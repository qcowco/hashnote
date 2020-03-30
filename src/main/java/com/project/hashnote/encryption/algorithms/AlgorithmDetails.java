package com.project.hashnote.encryption.algorithms;

import com.project.hashnote.encryption.exceptions.IncorrectAlgorithmPropertiesException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public abstract class AlgorithmDetails {
    abstract String getMethod();
    abstract String getMode();
    abstract String getPadding();
    abstract int getKeySize();
    abstract int getKeyLength();
    abstract int getVectorSize();

    public boolean isMethod(String method) {
        return getMethod().equals(method);
    }

    public SecretKey randomSecretKey() {
        KeyGenerator keyGenerator = tryGetKeyGenerator();
        SecureRandom secureRandom = new SecureRandom();
        keyGenerator.init(getKeySize(), secureRandom);

        return keyGenerator.generateKey();
    }

    private KeyGenerator tryGetKeyGenerator() {
        try {
            return KeyGenerator.getInstance(getMethod());
        } catch (NoSuchAlgorithmException e) {
            throw new IncorrectAlgorithmPropertiesException("Incorrect algorithm.", e);
        }
    }

    public SecretKey customSecretKey(byte[] customKey) {
        return new SecretKeySpec(customKey, 0, customKey.length, getMethod());
    }

    public boolean isKeyProperLength(byte[] key) {
        return key.length == getKeyLength();
    }

    public IvParameterSpec randomInitVector() {
        byte[] randomBytes = new byte[getVectorByteSize()];
        new SecureRandom().nextBytes(randomBytes);

        return new IvParameterSpec(randomBytes);
    }

    private int getVectorByteSize() {
        return getVectorSize()/8;
    }

    public IvParameterSpec customInitVector(byte[] customVector) {
        return new IvParameterSpec(customVector);
    }

    public boolean isVectorProperLength(byte[] vector) {
        return vector.length == getVectorByteSize();
    }

    public Cipher getCipher() {
        return tryGetCipher();
    }

    private Cipher tryGetCipher()  {
        try {
            String transformation = getMethod()
                    + "/" + getMode() + "/" + getPadding();

            return Cipher.getInstance(transformation);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IncorrectAlgorithmPropertiesException
                    ("Provided algorithm's properties don't match up with expected values", e);
        }
    }
}
