package com.project.hashnote.encoders;

import com.project.hashnote.encoders.algorithms.AlgorithmDetails;
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
            return getEncodedFrom(plainMessage);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] getEncodedFrom(byte[] plainMessage) throws InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, initVector);

        byte[] plainBytes = plainMessage;
        byte[] encodedBytes = cipher.doFinal(plainBytes);

        return encodedBytes;
    }

    public byte[] decode(byte[] encodedMessage) throws BadPaddingException {
        return tryDecode(encodedMessage);
    }

    private byte[] tryDecode(byte[] encodedMessage) throws BadPaddingException {
        try {
            return getDecodedFrom(encodedMessage);
        } catch (InvalidKeyException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] getDecodedFrom(byte[] encodedMessage) throws InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.DECRYPT_MODE, secretKey, initVector);

        byte[] encodedBytes = encodedMessage;
        byte[] plainBytes = cipher.doFinal(encodedBytes);

        return plainBytes;
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

        public EncoderBuilder secretKey() throws NoSuchAlgorithmException {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm.getMethod());
            SecureRandom secureRandom = new SecureRandom();
            keyGenerator.init(algorithm.getKeySize(), secureRandom);

            secretKey = keyGenerator.generateKey();

            return this;
        }

        public EncoderBuilder secretKey(byte[] customKey){
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

        private void cipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
            cipher = Cipher.getInstance(algorithm.getMethod()
                    + "/" + algorithm.getMode() + "/" + algorithm.getPadding());
        }

        public MessageEncoderImpl build() throws NoSuchAlgorithmException, NoSuchPaddingException {
            if (secretKey == null)
                secretKey();

            if (initVector == null)
                initVector();

            cipher();

            return new MessageEncoderImpl(cipher, secretKey, initVector);
        }
    }
}
