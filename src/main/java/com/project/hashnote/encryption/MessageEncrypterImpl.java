package com.project.hashnote.encryption;

import com.project.hashnote.encryption.algorithms.AlgorithmDetails;
import com.project.hashnote.encryption.exceptions.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageEncrypterImpl implements MessageEncrypter {
    private Cipher cipher;
    private SecretKey secretKey;
    private IvParameterSpec initVector;

    public  byte[] encrypt(byte[] message) {
        trySetCipherMode(Cipher.ENCRYPT_MODE);
        return tryEncrypt(message);
    }

    private byte[] tryEncrypt(byte[] plainMessage) {
        try {
            return executeAlgorithmFor(plainMessage);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new IllegalStateException("Internal error.", e);
        }
    }

    public byte[] decrypt(byte[] encryptedMessage) {
        trySetCipherMode(Cipher.DECRYPT_MODE);
        return tryDecrypt(encryptedMessage);
    }

    private byte[] tryDecrypt(byte[] encryptedMessage) {
        try {
            return executeAlgorithmFor(encryptedMessage);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            throw new IncorrectPrivateKeyException("The provided key was incorrect", e);
        }
    }

    private void trySetCipherMode(int operationMode){
        try {
            setCipherMode(operationMode);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new IllegalStateException("Internal error.", e);
        }
    }

    private void setCipherMode(int operationMode) throws InvalidAlgorithmParameterException, InvalidKeyException {
        cipher.init(operationMode, secretKey, initVector);
    }

    private byte[] executeAlgorithmFor(byte[] message) throws IllegalBlockSizeException, BadPaddingException{
        return cipher.doFinal(message);
    }

    public byte[] getSecretKey(){
        return secretKey.getEncoded();
    }

    public byte[] getInitVector(){
        return initVector.getIV();
    }

    public String getMethod() { return secretKey.getAlgorithm(); }

    public static EncrypterBuilder builder(){
        return new EncrypterBuilder();
    }

    public static class EncrypterBuilder {
        private AlgorithmDetails algorithm;
        private Cipher cipher;
        private SecretKey secretKey;
        private IvParameterSpec initVector;

        public EncrypterBuilder(){}

        public EncrypterBuilder algorithmDetails(AlgorithmDetails algorithm){
            this.algorithm = algorithm;
            return this;
        }

        public EncrypterBuilder secretKey() {
            KeyGenerator keyGenerator = tryGetKeyGeneratorForAlgorithm();
            SecureRandom secureRandom = new SecureRandom();
            keyGenerator.init(algorithm.getKeySize(), secureRandom);

            secretKey = keyGenerator.generateKey();

            return this;
        }

        private KeyGenerator tryGetKeyGeneratorForAlgorithm() {
            try {
                return KeyGenerator.getInstance(algorithm.getMethod());
            } catch (NoSuchAlgorithmException e) {
                throw new IncorrectAlgorithmPropertiesException("Incorrect algorithm.", e);
            }
        }

        public EncrypterBuilder secretKey(byte[] customKey) {
            secretKey = new SecretKeySpec(customKey, 0, customKey.length, algorithm.getMethod());

            verifyKey(secretKey.getEncoded());

            return this;
        }

        private void verifyKey(byte[] customKey) {
            if(customKey.length != algorithm.getKeyByteSize())
                throw new MalformedPrivateKeyException("Key needs to be "
                        + algorithm.getKeyByteSize() + " bytes long, is: " + customKey.length);
        }


        public EncrypterBuilder initVector(){
            byte[] randomBytes = new byte[algorithm.getVectorByteSize()];
            new SecureRandom().nextBytes(randomBytes);

            initVector = new IvParameterSpec(randomBytes);

            return this;
        }

        public EncrypterBuilder initVector(byte[] customVector){
            verifyVector(customVector);

            initVector = new IvParameterSpec(customVector);

            return this;
        }

        private void verifyVector(byte[] customVector) {
            if(customVector.length != algorithm.getVectorByteSize())
                throw new MalformedVectorException("Vector needs to be "
                        + algorithm.getVectorByteSize() + " bytes long, is: " + customVector.length);
        }

        private void cipher() {
            tryGetCipherForAlgorithm();
        }

        private void tryGetCipherForAlgorithm()  {
            try {
                String transformation = algorithm.getMethod()
                        + "/" + algorithm.getMode() + "/" + algorithm.getPadding();

                cipher = Cipher.getInstance(transformation);
            } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                throw new IncorrectAlgorithmPropertiesException
                        ("Provided algorithm's properties don't match up with expected values", e);
            }
        }

        public MessageEncrypterImpl build() {
            if (secretKey == null)
                secretKey();

            if (initVector == null)
                initVector();

            cipher();

            return new MessageEncrypterImpl(cipher, secretKey, initVector);
        }
    }
}
