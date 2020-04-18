package com.project.hashnote.note.util;

import com.project.hashnote.encryption.MessageEncrypter;
import com.project.hashnote.encryption.MessageEncrypterBuilder;
import com.project.hashnote.encryption.algorithms.AlgorithmDetails;
import com.project.hashnote.encryption.exceptions.InvalidAlgorithmNameException;
import com.project.hashnote.note.document.Note;
import com.project.hashnote.note.dto.EncryptionCredentials;
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
    public EncryptionCredentials encrypt(NoteRequest noteRequest) {
        EncryptionCredentials encryptionCredentials = encryptionMapper.getEncryptionDetails(noteRequest);

        MessageEncrypter messageEncrypter = buildEncrypterFor(encryptionCredentials);

        messageEncrypter.encrypt();

        EncryptionCredentials result = messageEncrypter.getEncryptionCredentials();

        return noteEncoder.encode(result);
    }

    private MessageEncrypter buildEncrypterFor(EncryptionCredentials encryptionCredentials){
        AlgorithmDetails algorithmDetails = tryGetAlgorithm(encryptionCredentials.getMethod());

        builder.algorithmDetails(algorithmDetails);
        builder.encryptionCredentials(encryptionCredentials);

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
        EncryptionCredentials encodedDetails = encryptionMapper.noteAndKeyToEncryption(note, secretKey);

        EncryptionCredentials decodedDetails = noteEncoder.decode(encodedDetails);

        MessageEncrypter messageEncrypter = buildEncrypterFor(decodedDetails);

        messageEncrypter.decrypt();

        return messageEncrypter.getEncryptionCredentials().getMessage();
    }
}
