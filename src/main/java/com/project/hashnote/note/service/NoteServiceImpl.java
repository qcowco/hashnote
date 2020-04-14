package com.project.hashnote.note.service;

import com.project.hashnote.note.dto.EncryptionResponse;
import com.project.hashnote.note.exception.NoteExpiredException;
import com.project.hashnote.note.exception.UnlockLimitExceededException;
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
    public EncryptionResponse save(NoteRequest noteRequest, String username) {
        hasUniqueId(noteRequest);

        return saveRequest(noteRequest, username);
    }

    private void hasUniqueId(NoteRequest noteRequest) {
        if(noteRequest.hasNoteId() && noteExists(noteRequest.getId()))
            throw new IllegalArgumentException("There's already a note with that id.");
    }

    private boolean noteExists(String id) {
        return noteRepository.existsById(id);
    }

    private EncryptionResponse saveRequest(NoteRequest noteRequest, String username) {
        EncryptionDetails encryptionDetails = encryptRequest(noteRequest);
        Note note = getNote(noteRequest, username);

        encryptionMapper.copyEncryptionDetails(encryptionDetails, note);

        Note persistedNote = noteRepository.save(note);

        return new EncryptionResponse(persistedNote.getId(), new String(encryptionDetails.getSecretKey()));
    }

    private Note getNote(NoteRequest noteRequest, String username) {
        Note noteDto = noteMapper.noteDtoToNote(noteRequest.getNoteDto());
        Note note = noteMapper.requestToNote(noteRequest);

        noteMapper.copyNote(noteDto, note);

        note.setAuthor(username);

        return note;
    }

    private EncryptionDetails encryptRequest(NoteRequest noteRequest) {
        EncryptionDetails requestEncryption = encryptionMapper.getEncryptionDetails(noteRequest);
        EncryptionDetails resultEncryption = noteEncrypter.encrypt(requestEncryption);

        return noteEncoder.encode(resultEncryption);
    }

    @Override
    public List<NoteDto> getAllBy(String username) {
        List<Note> notes = noteRepository.findByAuthor(username);

        return noteMapper.noteToNoteDtoList(notes);
    }

    @Override
    public NoteDto getEncrypted(String id) {
        Note note = tryGetNote(id);

        return noteMapper.noteToNoteDto(note);
    }

    @Override
    public NoteDto getDecrypted(String id, String secretKey) {
        Note note = tryGetNote(id);

        byte[] decryptedMessage = tryUnlockNote(secretKey, note);

        return noteMapper.noteAndMessageToNoteDto(note, decryptedMessage);
    }

    private Note tryGetNote(String id) {
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
        isLimitExceeded(note);
        isExpired(note);
    }

    private void isLimitExceeded(Note note) {
        if (note.getMaxVisits() > 0 && note.getKeyVisits() >= note.getMaxVisits())
            throw new UnlockLimitExceededException("Note unlock limit exceeded.");
    }

    private void isExpired(Note note) {
        if (note.getExpiresAt() != null && note.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new NoteExpiredException("Note expired at: " + note.getExpiresAt());
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
    public EncryptionResponse patch(NoteRequest noteRequest, String username, String id, String secretKey) {
        Note note = tryGetNoteForUser(username, id);

        byte[] message = decryptNote(note, secretKey);

        NoteRequest originalRequest = noteMapper.noteToRequest(note);
        originalRequest.getNoteDto().setMessage(new String(message));

        noteMapper.copyProperties(noteRequest, originalRequest);

        return saveRequest(originalRequest, username);
    }

    private Note tryGetNoteForUser(String username, String id) {
        Optional<Note> optionalNote = noteRepository.findByAuthorAndId(username, id);
        return optionalNote.orElseThrow(
                () -> new ResourceNotFoundException("No such note found for this user.")
        );
    }

    @Override
    public void delete(String id, String username) {
        tryGetNoteForUser(username, id);
        delete(id);
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
