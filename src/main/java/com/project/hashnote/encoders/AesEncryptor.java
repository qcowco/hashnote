package com.project.hashnote.encoders;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Random;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AesEncryptor {
    private static final String method = "AES";
    private static final String mode = "CBC";
    private static final String padding = "PKCS5Padding";
    private static final int keySize = 256;
    private static final int vectorSize = 16;

    private Cipher cipher;
    private SecretKey secretKey;
    private IvParameterSpec initVector;

    public byte[] encode(byte[] message) throws InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, initVector);
        return cipher.doFinal(message);
    }

    public byte[] decode(byte[] encodedMessage) throws InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.DECRYPT_MODE, secretKey, initVector);
        return cipher.doFinal(encodedMessage);
    }

    public SecretKey getPrivateKey(){
        return secretKey;
    }

    public IvParameterSpec getInitVector(){
        return initVector;
    }

    public static EncryptorBuilder builder() {
        return new EncryptorBuilder();
    }

    public static class EncryptorBuilder {
        private Cipher cipher;
        private SecretKey secretKey;
        private IvParameterSpec initVector;

        public EncryptorBuilder(){}

        public EncryptorBuilder secretKey() throws NoSuchAlgorithmException {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(method);
            keyGenerator.init(keySize);
            secretKey = keyGenerator.generateKey();
            return this;
        }

        public EncryptorBuilder secretKey(byte[] customKey){
            secretKey = new SecretKeySpec(customKey, 0, customKey.length, method);
            return this;
        }

        public EncryptorBuilder initVector(){
            byte[] randomVector = new byte[vectorSize];
            new Random().nextBytes(randomVector);
            initVector = new IvParameterSpec(randomVector);
            return this;
        }

        public EncryptorBuilder initVector(byte[] customVector){
            if(customVector.length != vectorSize)
                throw new IllegalArgumentException("Vector needs to be 16 bytes long, is: " + customVector.length);
            initVector = new IvParameterSpec(customVector);
            return this;
        }

        private void cipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
            cipher = Cipher.getInstance(method + "/" + mode + "/" + padding);
        }

        public AesEncryptor build() throws NoSuchAlgorithmException, NoSuchPaddingException {
            if (secretKey == null)
                secretKey();

            if (initVector == null)
                initVector();

            cipher();

            return new AesEncryptor(cipher, secretKey, initVector);
        }
    }
}
