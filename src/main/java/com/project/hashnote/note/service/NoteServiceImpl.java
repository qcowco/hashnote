package com.project.hashnote.note.service;

import com.project.hashnote.note.mapper.EncryptionMapper;
import com.project.hashnote.note.util.NoteEncoder;
import com.project.hashnote.note.util.NoteEncrypter;
import com.project.hashnote.note.dao.NoteRepository;
import com.project.hashnote.note.document.Note;
import com.project.hashnote.note.dto.EncryptionDetails;
import com.project.hashnote.note.dto.NoteDto;
import com.project.hashnote.note.dto.NoteRequest;
import com.project.hashnote.note.mapper.NoteMapper;
import com.project.hashnote.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NoteServiceImpl implements NoteService {

    private NoteEncoder noteEncoder;
    private NoteEncrypter noteEncrypter;
    private NoteRepository noteRepository;
    private NoteMapper noteMapper;
    private EncryptionMapper encryptionMapper;

    @Autowired
    public NoteServiceImpl(NoteEncoder noteEncoder, NoteEncrypter noteEncrypter, NoteRepository noteRepository,
                           NoteMapper noteMapper, EncryptionMapper encryptionMapper) {
        this.noteEncoder = noteEncoder;
        this.noteEncrypter = noteEncrypter;
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
        this.encryptionMapper = encryptionMapper;
    }

    @Override
    public String save(NoteRequest noteRequest) {
        if(noteRequest.hasNoteId() && noteExists(noteRequest.getId()))
            throw new IllegalArgumentException("There's already a note with id: " + noteRequest.getId());

        return saveRequest(noteRequest);
    }

    private boolean noteExists(String id) {
        return noteRepository.findById(id).isPresent();
    }

    private String saveRequest(NoteRequest noteRequest) {
        EncryptionDetails requestEncryption = encryptionMapper.getEncryptionDetails(noteRequest);

        EncryptionDetails resultEncryption = noteEncrypter.encrypt(requestEncryption);

        EncryptionDetails encodedEncryption = noteEncoder.encode(resultEncryption);

        Note note = noteMapper.requestToNote(noteRequest);
        encryptionMapper.copyProperties(encodedEncryption, note);

        Note persistedNote = noteRepository.save(note);

        return persistedNote.getId() + "/" + new String(encodedEncryption.getSecretKey());
    }

    @Override
    public List<NoteDto> getAll() {
        List<Note> notes = noteRepository.findAll();

        return noteMapper.noteToNoteDtoList(notes);
    }

    @Override
    public NoteDto getEncrypted(String id) {
        Note note = tryGetNoteById(id);

        return noteMapper.noteToNoteDto(note);
    }

    @Override
    public NoteDto getDecrypted(String id, String secretKey) {
        Note note = tryGetNoteById(id);

        EncryptionDetails encodedDetails = encryptionMapper.noteAndKeyToEncryption(note, secretKey);
        EncryptionDetails encryptedDetails = noteEncoder.decode(encodedDetails);

        byte[] decryptedMessage = noteEncrypter.decrypt(encryptedDetails);

        note.setMessage(new String(decryptedMessage));

        return noteMapper.noteToNoteDto(note);
    }


    private Note tryGetNoteById(String id) {
        Optional<Note> optionalNote = noteRepository.findById(id);

        return optionalNote.orElseThrow(
                () -> new ResourceNotFoundException("No note found with id: " + id)
        );
    }

    @Override
    public String patch(String method, String id, String secretKey) {
        tryGetNoteById(id);

        NoteDto decryptedDto = getDecrypted(id, secretKey);

        NoteRequest noteRequest = new NoteRequest();
        noteRequest.setNoteDto(decryptedDto);
        noteRequest.setMethod(method);

        return saveRequest(noteRequest);
    }

    @Override
    public void delete(String id) {
        noteRepository.deleteById(id);
    }
}
