package com.project.hashnote.encoders;

import com.project.hashnote.encoders.algorithms.AlgorithmDetails;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageEncoderImpl implements MessageEncoder {
    private Cipher cipher;
    private SecretKey secretKey;
    private IvParameterSpec initVector;

    public byte[] encode(byte[] message) {
        return tryEncode(message);
    }

    private byte[] tryEncode(byte[] message) {
        byte[] encodedMesssage;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, initVector);
            encodedMesssage = cipher.doFinal(message);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
        return encodedMesssage;
    }

    public byte[] decode(byte[] encodedMessage) throws BadPaddingException {
        return tryDecodeMessage(encodedMessage);
    }

    private byte[] tryDecodeMessage(byte[] encodedMessage) throws BadPaddingException {
        byte[] decodedMessage;
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, initVector);
            decodedMessage = cipher.doFinal(encodedMessage);
        } catch (InvalidKeyException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
        return decodedMessage;
    }

    public byte[] getPrivateKey(){
        return secretKey.getEncoded();
    }

    public byte[] getInitVector(){
        return initVector.getIV();
    }

    public static EncoderBuilder builder(){
        return new EncoderBuilder();
    }

    public static class EncoderBuilder {
        private AlgorithmDetails details;
        private Cipher cipher;
        private SecretKey secretKey;
        private IvParameterSpec initVector;

        public EncoderBuilder(){}

        public EncoderBuilder algorithmDetails(AlgorithmDetails details){
            this.details = details;
            return this;
        }

        public EncoderBuilder secretKey() throws NoSuchAlgorithmException {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(details.getMethod());
            keyGenerator.init(details.getKeySize());
            secretKey = keyGenerator.generateKey();
            return this;
        }

        public EncoderBuilder secretKey(byte[] customKey){
            secretKey = new SecretKeySpec(customKey, 0, customKey.length, details.getMethod());
            return this;
        }

        public EncoderBuilder initVector(){
            byte[] randomVector = new byte[details.getVectorSize()];
            new Random().nextBytes(randomVector);
            initVector = new IvParameterSpec(randomVector);
            return this;
        }

        public EncoderBuilder initVector(byte[] customVector){
            if(customVector.length != details.getVectorSize())
                throw new IllegalArgumentException("Vector needs to be " + details.getVectorSize() + " bytes long, is: " + customVector.length);
            initVector = new IvParameterSpec(customVector);
            return this;
        }

        private void cipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
            cipher = Cipher.getInstance(details.getMethod() + "/" + details.getMode() + "/" + details.getPadding());
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
