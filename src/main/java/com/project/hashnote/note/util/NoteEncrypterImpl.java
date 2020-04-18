package com.project.hashnote.note.util;

import com.project.hashnote.encryption.MessageEncrypter;
import com.project.hashnote.encryption.MessageEncrypterBuilder;
import com.project.hashnote.encryption.algorithms.AlgorithmDetails;
import com.project.hashnote.encryption.exceptions.InvalidAlgorithmNameException;
import com.project.hashnote.note.document.Note;
import com.project.hashnote.note.dto.EncryptionDetails;
import com.project.hashnote.note.dto.NoteRequest;
import com.project.hashnote.note.mapper.EncryptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NoteEncrypterImpl implements NoteEncrypter {
    private List<AlgorithmDetails> algorithms;
    private MessageEncrypterBuilder builder;
    private EncryptionMapper encryptionMapper;
    private NoteEncoder noteEncoder;

    @Autowired
    public NoteEncrypterImpl(List<AlgorithmDetails> algorithms, MessageEncrypterBuilder builder,
                             EncryptionMapper encryptionMapper, NoteEncoder noteEncoder) {
        this.algorithms = algorithms;
        this.builder = builder;
        this.encryptionMapper = encryptionMapper;
        this.noteEncoder = noteEncoder;
    }

    @Override
    public EncryptionDetails encrypt(NoteRequest noteRequest) {
        EncryptionDetails encryptionDetails = encryptionMapper.getEncryptionDetails(noteRequest);

        MessageEncrypter messageEncrypter = buildEncrypterFor(encryptionDetails);

        messageEncrypter.encrypt();

        EncryptionDetails result = messageEncrypter.getEncryptionDetails();

        return noteEncoder.encode(result);
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
    public byte[] decrypt(Note note, String secretKey) {
        EncryptionDetails encodedDetails = encryptionMapper.noteAndKeyToEncryption(note, secretKey);

        EncryptionDetails decodedDetails = noteEncoder.decode(encodedDetails);

        MessageEncrypter messageEncrypter = buildEncrypterFor(decodedDetails);

        messageEncrypter.decrypt();

        return messageEncrypter.getEncryptionDetails().getMessage();
    }
}
