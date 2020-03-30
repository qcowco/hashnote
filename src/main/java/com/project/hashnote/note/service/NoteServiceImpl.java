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

import java.time.LocalDateTime;
import java.util.List;

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
    public String save(NoteRequest noteRequest, String username) {
        if(noteRequest.hasNoteId() && noteExists(noteRequest.getId()))
            throw new IllegalArgumentException("There's already a note with id: " + noteRequest.getId());

        return saveRequest(noteRequest, username);
    }

    private boolean noteExists(String id) {
        return noteRepository.findById(id).isPresent();
    }

    private String saveRequest(NoteRequest noteRequest, String username) {
        EncryptionDetails requestEncryption = encryptionMapper.getEncryptionDetails(noteRequest);

        EncryptionDetails resultEncryption = noteEncrypter.encrypt(requestEncryption);

        EncryptionDetails encodedEncryption = noteEncoder.encode(resultEncryption);

        Note note = noteMapper.requestToNote(noteRequest);

        note.setAuthor(username);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.plusMinutes(noteRequest.getMinutesToExpiration());

        note.setCreatedAt(now);
        if (noteRequest.getMinutesToExpiration() > 0)
            note.setExpiresAt(expired);

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
    public List<NoteDto> getAllBy(String username) {
        List<Note> notes = noteRepository.findByAuthor(username);

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

        byte[] decryptedMessage = tryUnlockNote(secretKey, note);

        return noteMapper.noteAndMessageToNoteDto(note, decryptedMessage);
    }


    private Note tryGetNoteById(String id) {
        return noteRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("No note found with id: " + id)
        );
    }

    private byte[] tryUnlockNote(String secretKey, Note note) {
        isNoteUnlockable(note);

        byte[] decryptedMessage = decryptNote(note, secretKey);

        incrementNoteVisits(note);

        return decryptedMessage;
    }

    private void isNoteUnlockable(Note note) {
        if (note.getMaxVisits() > 0 && note.getKeyVisits() >= note.getMaxVisits())
            throw new UnlockLimitExceededException("Note unlock limit exceeded.");
    }

    private byte[] decryptNote(Note note, String secretKey) {
        EncryptionDetails encryptedDetails = getEncryptionDetails(note, secretKey);

        return noteEncrypter.decrypt(encryptedDetails);
    }

    private EncryptionDetails getEncryptionDetails(Note note, String secretKey) {
        EncryptionDetails encodedDetails = encryptionMapper.noteAndKeyToEncryption(note, secretKey);
        return noteEncoder.decode(encodedDetails);
    }

    private void incrementNoteVisits(Note note) {
        note.setKeyVisits(note.getKeyVisits() + 1);

        noteRepository.save(note);
    }

    @Override
    public String patch(String method, String username, String id, String secretKey) {
        tryGetNoteById(id);

        byte[] message = decryptNote(note, secretKey);

        NoteRequest noteRequest = new NoteRequest();
        noteRequest.setNoteDto(decryptedDto);
        noteRequest.setMethod(method);

        return saveRequest(noteRequest, username);
    }

    @Override
    public void delete(String id) {
        noteRepository.deleteById(id);
    }

    @Override
    public List<NoteDto> findExpired() {
        List<Note> expiredNotes = noteRepository.findByExpiresAtBefore(LocalDateTime.now());
        return noteMapper.noteToNoteDtoList(expiredNotes);
    }

    @Override
    public List<NoteDto> findLimited() {
        List<Note> limitedNotes = noteRepository.findAllLimitedNotes();
        return noteMapper.noteToNoteDtoList(limitedNotes);
    }
}
