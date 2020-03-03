package com.project.hashnote.note.util;

import com.project.hashnote.encryption.MessageEncrypter;
import com.project.hashnote.encryption.MessageEncrypterBuilder;
import com.project.hashnote.encryption.algorithms.AlgorithmDetails;
import com.project.hashnote.encryption.exceptions.InvalidAlgorithmNameException;
import com.project.hashnote.note.dto.EncryptionDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NoteEncrypterImpl implements NoteEncrypter {
    private List<AlgorithmDetails> algorithms;
    private MessageEncrypterBuilder builder;

    @Autowired
    public NoteEncrypterImpl(List<AlgorithmDetails> algorithms, MessageEncrypterBuilder builder) {
        this.algorithms = algorithms;
        this.builder = builder;
    }

    @Override
    public EncryptionDetails encrypt(EncryptionDetails encryptionDetails) {
        MessageEncrypter messageEncrypter = buildEncrypterFor(encryptionDetails);

        messageEncrypter.encrypt(encryptionDetails.getMessage());

        return messageEncrypter.getEncryptionDetails();
    }

    private MessageEncrypter buildEncrypterFor(EncryptionDetails encryptionDetails){
        AlgorithmDetails algorithmDetails = tryGetAlgorithm(encryptionDetails.getMethod());

        builder.algorithmDetails(algorithmDetails);
        builder.encryptionDetails(encryptionDetails);

        return builder.build();
    }

    private AlgorithmDetails tryGetAlgorithm(String method) {
        return algorithms.stream()
                .filter(alg -> alg.isMethod(method))
                .findFirst()
                .orElseThrow(
                        () -> new InvalidAlgorithmNameException("No algorithm found with name: " + method)
                );
    }

    @Override
    public byte[] decrypt(EncryptionDetails encryptionDetails) {
        MessageEncrypter messageEncrypter = buildEncrypterFor(encryptionDetails);

        messageEncrypter.decrypt(encryptionDetails.getMessage());

        return messageEncrypter.getEncryptionDetails().getMessage();
    }
}
