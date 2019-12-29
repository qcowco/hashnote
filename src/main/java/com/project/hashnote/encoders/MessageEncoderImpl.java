package com.project.hashnote.encoders;

import com.project.hashnote.encoders.algorithms.AlgorithmDetails;
import com.project.hashnote.encoders.exceptions.IncorrectAlgorithmPropertiesException;
import com.project.hashnote.encoders.exceptions.IncorrectPrivateKeyException;
import com.project.hashnote.encoders.exceptions.MalformedPrivateKeyException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageEncoderImpl implements MessageEncoder {
    private Cipher cipher;
    private SecretKey secretKey;
    private IvParameterSpec initVector;

    public  byte[] encode(byte[] message) {
        return tryEncode(message);
    }

    private byte[] tryEncode(byte[] plainMessage) {
        try {
            return executeAlgorithm(Cipher.ENCRYPT_MODE, plainMessage);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException
                | IllegalBlockSizeException | BadPaddingException e) {
            throw new IllegalStateException("Internal error.", e);
        }
    }

    public byte[] decode(byte[] encodedMessage) throws IncorrectPrivateKeyException {
        return tryDecode(encodedMessage);
    }

    private byte[] tryDecode(byte[] encodedMessage) throws IncorrectPrivateKeyException {
        try {
            return executeAlgorithm(Cipher.DECRYPT_MODE, encodedMessage);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            throw new IncorrectPrivateKeyException("The provided key was incorrect", e);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new IllegalStateException("Internal error.", e);
        }
    }

    private byte[] executeAlgorithm(int operationMode, byte[] message) throws InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
        cipher.init(operationMode, secretKey, initVector);

        byte[] result = cipher.doFinal(message);

        return result;
    }

    public byte[] getPrivateKey(){
        return Base64.encodeBase64(secretKey.getEncoded());
    }

    public byte[] getInitVector(){
        return initVector.getIV();
    }

    public static EncoderBuilder builder(){
        return new EncoderBuilder();
    }

    public static class EncoderBuilder {
        private AlgorithmDetails algorithm;
        private Cipher cipher;
        private SecretKey secretKey;
        private IvParameterSpec initVector;

        public EncoderBuilder(){}

        public EncoderBuilder algorithmDetails(AlgorithmDetails details){
            this.algorithm = details;
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
            if(!Base64.isBase64(customKey))
                throw new MalformedPrivateKeyException("Provided key is malformed.");
            byte[] key = Base64.decodeBase64(customKey);

            secretKey = new SecretKeySpec(key, 0, key.length, algorithm.getMethod());

            return this;
        }

        public EncoderBuilder initVector(){
            byte[] randomBytes = new byte[algorithm.getVectorSize()];
            new Random().nextBytes(randomBytes);

            initVector = new IvParameterSpec(randomBytes);

            return this;
        }

        public EncoderBuilder initVector(byte[] customVector){
            if(customVector.length != algorithm.getVectorSize())
                throw new IllegalArgumentException("Vector needs to be "
                        + algorithm.getVectorSize() + " bytes long, is: " + customVector.length);

            initVector = new IvParameterSpec(customVector);

            return this;
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
                throw new IncorrectAlgorithmPropertiesException("Incorrect algorithm.", e);
            }
        }

        public MessageEncoderImpl build() {
            if (secretKey == null)
                secretKey();

            if (initVector == null)
                initVector();

            cipher();

            return new MessageEncoderImpl(cipher, secretKey, initVector);
        }
    }
}
