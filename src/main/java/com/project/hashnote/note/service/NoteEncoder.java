package com.project.hashnote.service;

import com.project.hashnote.document.Note;
import com.project.hashnote.note.dto.EncodingDetails;
import com.project.hashnote.note.dto.NoteDto;
import com.project.hashnote.note.dto.NoteRequest;
import com.project.hashnote.encoders.MessageEncoder;
import com.project.hashnote.encoders.MessageEncoderImpl;
import com.project.hashnote.encoders.algorithms.AlgorithmDetails;
import com.project.hashnote.encoders.exceptions.InvalidAlgorithmNameException;
import com.project.hashnote.encoders.exceptions.MalformedPrivateKeyException;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.apache.tomcat.util.codec.binary.Base64.*;

@Component
public class NoteEncoder {
    private List<AlgorithmDetails> algorithms;

    @Autowired
    public NoteEncoder(List<AlgorithmDetails> algorithms) {
        this.algorithms = algorithms;
    }

    public NoteRequest encodeRequest(NoteRequest noteRequest) {
        EncodingDetails encodingDetails = noteRequest.getEncodingDetails();

        MessageEncoder messageEncoder = getEncoderFor(encodingDetails);

        NoteDto plainDto = noteRequest.getNoteDto();
        byte[] encryptedMessage = messageEncoder.encode(plainDto.getContent().getBytes());
        NoteDto encodedDto = createEncodedDto(plainDto, encryptedMessage);

        EncodingDetails encodedDetails = createEncodedDetails(messageEncoder);
        encodedDetails.setMethod(messageEncoder.getMethod());

        return new NoteRequest(encodedDto, encodedDetails);
    }

    private MessageEncoderImpl getEncoderFor(EncodingDetails encodingDetails) {
        AlgorithmDetails algorithmDetails = tryGetAlgorithm(encodingDetails.getMethod());

        MessageEncoderImpl.EncoderBuilder builder = MessageEncoderImpl.builder()
                .algorithmDetails(algorithmDetails);

        if (encodingDetails.getKey() != null)
            builder = builder.secretKey(decodeBase64(decodeBase64(encodingDetails.getKey())));

        if (encodingDetails.getVector() != null)
            builder = builder.initVector(decodeBase64(encodingDetails.getVector()));
// TODO: 29.12.2019 oddzielic enkodowanie od buildowania
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

    private NoteDto createEncodedDto(NoteDto noteDto, byte[] encryptedMessage) {
        NoteDto encodedDto = new NoteDto();

        encodedDto.setId(noteDto.getId());
        encodedDto.setName(noteDto.getName());
        encodedDto.setContent(encodeBase64String(encryptedMessage));

        return encodedDto;
    }

    private EncodingDetails createEncodedDetails(MessageEncoder messageEncoder) {
        EncodingDetails encodingDetails = new EncodingDetails();

        String[] encodedDetails = getEncodedDetailsFor(messageEncoder);

        String encodedKey = encodedDetails[0];
        String encodedVector = encodedDetails[1];

        encodingDetails.setKey(encodedKey);
        encodingDetails.setVector(encodedVector);

        return encodingDetails;
    }

    private String[] getEncodedDetailsFor(MessageEncoder messageEncoder) {
        String[] encodedDetails = new String[2];

        encodedDetails[0] = encodeBase64String(encodeBase64(messageEncoder.getPrivateKey()));
        encodedDetails[1] = encodeBase64String(messageEncoder.getInitVector());

        return encodedDetails;
    }


    public byte[] decrypt(Note note, String secretKey) {
        verifyKey(secretKey.getBytes());

        EncodingDetails encodingDetails = new EncodingDetails(secretKey,
                note.getEncodingDetails().getVector(), note.getEncodingDetails().getMethod());

        MessageEncoderImpl messageDecoder = getEncoderFor(encodingDetails);

        byte[] content = decodeBase64(note.getContent());

        return messageDecoder.decode(content);
    }

    private void verifyKey(byte[] customKey) {
        if(!Base64.isBase64(customKey))
            throw new MalformedPrivateKeyException("Provided key is malformed.");
    }
}
