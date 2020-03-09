package com.project.hashnote.encryption.algorithms;

import com.project.hashnote.encryption.exceptions.IncorrectAlgorithmPropertiesException;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Component
public class DES56Details implements AlgorithmDetails {
    private final String method = "DES";
    private final String mode = "CBC";
    private final String padding = "PKCS5Padding";
    private final int keySize = 56;
    private final int vectorSize = 64;

    @Override
    public boolean isMethod(String method) {
        return this.method.equals(method);
    }

    @Override
    public SecretKey randomSecretKey() {
        KeyGenerator keyGenerator = tryGetKeyGenerator();
        SecureRandom secureRandom = new SecureRandom();
        keyGenerator.init(keySize, secureRandom);

        return keyGenerator.generateKey();
    }

    private KeyGenerator tryGetKeyGenerator() {
        try {
            return KeyGenerator.getInstance(method);
        } catch (NoSuchAlgorithmException e) {
            throw new IncorrectAlgorithmPropertiesException("Incorrect algorithm.", e);
        }
    }

    @Override
    public SecretKey customSecretKey(byte[] customKey) {
        return new SecretKeySpec(customKey, 0, customKey.length, method);
    }

    @Override
    public boolean isKeyProperLength(byte[] key) {
        return key.length == (keySize + 8)/8;
    }

    public IvParameterSpec randomInitVector() {
        byte[] randomBytes = new byte[getVectorByteSize()];
        new SecureRandom().nextBytes(randomBytes);

        return new IvParameterSpec(randomBytes);
    }

    private int getVectorByteSize() {
        return vectorSize/8;
    }

    @Override
    public IvParameterSpec customInitVector(byte[] customVector) {
        return new IvParameterSpec(customVector);
    }

    @Override
    public boolean isVectorProperLength(byte[] vector) {
        return vector.length == getVectorByteSize();
    }

    @Override
    public Cipher getCipher() {
        return tryGetCipher();
    }

    private Cipher tryGetCipher()  {
        try {
            String transformation = method
                    + "/" + mode + "/" + padding;

            return Cipher.getInstance(transformation);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IncorrectAlgorithmPropertiesException
                    ("Provided algorithm's properties don't match up with expected values", e);
        }
    }
}
