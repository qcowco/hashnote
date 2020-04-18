package com.project.hashnote.encryption;

import com.project.hashnote.encryption.exceptions.*;
import com.project.hashnote.note.dto.EncryptionCredentials;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Setter;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
public class MessageEncrypterImpl implements MessageEncrypter {
    private byte[] message;
    private Cipher cipher;
    private SecretKey secretKey;
    private IvParameterSpec initVector;

    public void encrypt() {
        trySetCipherMode(Cipher.ENCRYPT_MODE);
        this.message = tryEncrypt();
    }

    private byte[] tryEncrypt() {
        try {
            return executeAlgorithm();
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new IllegalStateException("Internal error.", e);
        }
    }

    public void decrypt() {
        trySetCipherMode(Cipher.DECRYPT_MODE);
        message = tryDecrypt();
    }

    private byte[] tryDecrypt() {
        try {
            return executeAlgorithm();
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

    private byte[] executeAlgorithm() throws IllegalBlockSizeException, BadPaddingException{
        return cipher.doFinal(message);
    }

    public EncryptionCredentials getEncryptionCredentials() {
        EncryptionCredentials encryptionCredentials = new EncryptionCredentials();

        encryptionCredentials.setMessage(message);
        encryptionCredentials.setSecretKey(getSecretKey());
        encryptionCredentials.setVector(getInitVector());
        encryptionCredentials.setMethod(getMethod());

        return encryptionCredentials;
    }

    private byte[] getSecretKey(){
        return secretKey.getEncoded();
    }

    private byte[] getInitVector(){
        return initVector.getIV();
    }

    private String getMethod() { return secretKey.getAlgorithm(); }
}
