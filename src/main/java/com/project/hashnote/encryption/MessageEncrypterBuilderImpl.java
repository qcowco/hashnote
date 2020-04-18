package com.project.hashnote.encryption;

import com.project.hashnote.encryption.algorithms.AlgorithmDetails;
import com.project.hashnote.encryption.exceptions.MalformedPrivateKeyException;
import com.project.hashnote.encryption.exceptions.MalformedVectorException;
import com.project.hashnote.note.dto.EncryptionCredentials;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

@Component(value = "prototype")
public class MessageEncrypterBuilderImpl implements MessageEncrypterBuilder {
    private AlgorithmDetails algorithm;
    private EncryptionCredentials encryptionCredentials;

    private byte[] message;
    private Cipher cipher;
    private SecretKey secretKey;
    private IvParameterSpec initVector;

    @Override
    public MessageEncrypterBuilder algorithmDetails(AlgorithmDetails algorithmDetails) {
        this.algorithm = algorithmDetails;
        return this;
    }

    @Override
    public MessageEncrypterBuilder encryptionCredentials(EncryptionCredentials encryptionCredentials) {
        this.encryptionCredentials = encryptionCredentials;

        return this;
    }

    private void secretKey() {
        secretKey = algorithm.randomSecretKey();
    }

    private void secretKey(byte[] customKey) {
        secretKey = algorithm.customSecretKey(customKey);

        isKeyMalformed(secretKey.getEncoded());
    }

    private void isKeyMalformed(byte[] customKey) {
        if(!algorithm.isKeyProperLength(customKey))
            throw new MalformedPrivateKeyException("Incorrect key length.");
    }

    private void initVector() {
        initVector = algorithm.randomInitVector();
    }

    private void initVector(byte[] customVector){
        isVectorMalformed(customVector);
        initVector = algorithm.customInitVector(customVector);
    }

    private void isVectorMalformed(byte[] customVector) {
        if(!algorithm.isVectorProperLength(customVector))
            throw new MalformedVectorException("Incorrect vector length.");
    }

    private void cipher() {
        cipher = algorithm.getCipher();
    }

    @Override
    public MessageEncrypter build() {
        message = encryptionCredentials.getMessage();

        if(encryptionCredentials.getSecretKey() == null)
            secretKey();
        else
            secretKey(encryptionCredentials.getSecretKey());

        if(encryptionCredentials.getVector() == null)
            initVector();
        else
            initVector(encryptionCredentials.getVector());

        cipher();

        MessageEncrypterImpl messageEncrypter = new MessageEncrypterImpl(message, cipher, secretKey, initVector);

        clean();

        return messageEncrypter;
    }

    private void clean() {
        algorithm = null;
        encryptionCredentials = null;
        cipher = null;
        message = null;
        secretKey = null;
        initVector = null;
    }
}
