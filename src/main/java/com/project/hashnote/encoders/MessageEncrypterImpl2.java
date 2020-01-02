package com.project.hashnote.encoders;

import com.project.hashnote.encoders.algorithms.AlgorithmDetails;
import com.project.hashnote.encoders.exceptions.IncorrectPrivateKeyException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.List;

@AllArgsConstructor
@Component
@Scope("prototype")
public class MessageEncrypterImpl2 implements MessageEncrypter {
    private List<AlgorithmDetails> algorithms;
    private Cipher cipher;
    private SecretKey secretKey;
    private IvParameterSpec initVector;

    @Autowired
    public MessageEncrypterImpl2(List<AlgorithmDetails> algorithms) {
        this.algorithms = algorithms;
    }

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

    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public byte[] getSecretKey(){
        return secretKey.getEncoded();
    }

    public void setInitVector(IvParameterSpec initVector) {
        this.initVector = initVector;
    }

    public byte[] getInitVector(){
        return initVector.getIV();
    }

    public String getMethod() { return secretKey.getAlgorithm(); }
}
