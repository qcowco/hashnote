package com.project.hashnote.note.util;

import com.project.hashnote.encoders.MessageEncrypter;
import com.project.hashnote.encoders.MessageEncrypterImpl;
import com.project.hashnote.encoders.algorithms.AlgorithmDetails;
import com.project.hashnote.encoders.exceptions.InvalidAlgorithmNameException;
import com.project.hashnote.note.dto.NoteRequest;
import com.project.hashnote.note.dto.EncryptionDetails;
import com.project.hashnote.note.mapper.EncryptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NoteEncrypterImpl implements NoteEncrypter {
    private List<AlgorithmDetails> algorithms;
    private EncryptionMapper encryptionMapper;

    @Autowired
    public NoteEncrypterImpl(List<AlgorithmDetails> algorithms, EncryptionMapper encryptionMapper) {
        this.algorithms = algorithms;
        this.encryptionMapper = encryptionMapper;
    }

    @Override
    public EncryptionDetails encrypt(NoteRequest request) {

        EncryptionDetails encryptionDetails = encryptionMapper.getEncryptionDetails(request);

        MessageEncrypter messageEncoder = getEncoderFor(encryptionDetails);

        byte[] plainMessage = request.getContent().getBytes();
        byte[] encryptedMessage = messageEncoder.encode(plainMessage);

        return new EncryptionDetails(encryptedMessage, messageEncoder.getSecretKey(),
                messageEncoder.getInitVector(), messageEncoder.getMethod());
    }

    private MessageEncrypter getEncoderFor(EncryptionDetails encryptionDetails){
        AlgorithmDetails algorithmDetails = tryGetAlgorithm(encryptionDetails.getMethod());

        MessageEncrypterImpl.EncoderBuilder builder = MessageEncrypterImpl.builder()
                .algorithmDetails(algorithmDetails);

        if(encryptionDetails.getSecretKey() != null)
            builder.secretKey(encryptionDetails.getSecretKey());

        if(encryptionDetails.getVector() != null)
            builder.initVector(encryptionDetails.getVector());

        return builder.build();
    }

    private AlgorithmDetails tryGetAlgorithm(String method) {
        return algorithms.stream()
                .filter(alg -> alg.getMethod().equals(method))
                .findFirst()
                .orElseThrow(
                        () -> new InvalidAlgorithmNameException("No algorithm found with name: " + method)
                );
    }

    @Override
    public byte[] decrypt(EncryptionDetails encryptionDetails) {
        MessageEncrypter messageEncoder = getEncoderFor(encryptionDetails);

        byte[] encryptedMessage = encryptionDetails.getMessage();
        byte[] decryptedMessage = messageEncoder.decode(encryptedMessage);

        return decryptedMessage;
    }
}
