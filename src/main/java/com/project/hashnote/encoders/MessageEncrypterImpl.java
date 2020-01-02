package com.project.hashnote.encoders;

import com.project.hashnote.encoders.algorithms.AlgorithmDetails;
import com.project.hashnote.encoders.exceptions.*;
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

    public  byte[] encode(byte[] message) {
        trySetCipherMode(Cipher.ENCRYPT_MODE);
        return tryEncode(message);
    }

    private byte[] tryEncode(byte[] plainMessage) {
        try {
            return executeAlgorithmFor(plainMessage);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new IllegalStateException("Internal error.", e);
        }
    }

    public byte[] decode(byte[] encodedMessage) {
        trySetCipherMode(Cipher.DECRYPT_MODE);
        return tryDecode(encodedMessage);
    }

    private byte[] tryDecode(byte[] encodedMessage) {
        try {
            return executeAlgorithmFor(encodedMessage);
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

    public static EncoderBuilder builder(){
        return new EncoderBuilder();
    }

    public static class EncoderBuilder {
        private AlgorithmDetails algorithm;
        private Cipher cipher;
        private SecretKey secretKey;
        private IvParameterSpec initVector;

        public EncoderBuilder(){}

        public EncoderBuilder algorithmDetails(AlgorithmDetails algorithm){
            this.algorithm = algorithm;
            return this;
        }

        public EncoderBuilder secretKey() {
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

        public EncoderBuilder secretKey(byte[] customKey) {
            secretKey = new SecretKeySpec(customKey, 0, customKey.length, algorithm.getMethod());

            verifyKey(secretKey.getEncoded());

            return this;
        }

        private void verifyKey(byte[] customKey) {
            if(customKey.length != algorithm.getKeyByteSize())
                throw new MalformedPrivateKeyException("Key needs to be "
                        + algorithm.getKeyByteSize() + " bytes long, is: " + customKey.length);
        }


        public EncoderBuilder initVector(){
            byte[] randomBytes = new byte[algorithm.getVectorByteSize()];
            new SecureRandom().nextBytes(randomBytes);

            initVector = new IvParameterSpec(randomBytes);

            return this;
        }

        public EncoderBuilder initVector(byte[] customVector){
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
