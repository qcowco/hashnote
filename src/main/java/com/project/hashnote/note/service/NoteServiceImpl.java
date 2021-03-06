package com.project.hashnote.note.service;

import com.project.hashnote.note.dto.*;
import com.project.hashnote.note.exception.NoteExpiredException;
import com.project.hashnote.note.exception.UnlockLimitExceededException;
import com.project.hashnote.note.mapper.EncryptionMapper;
import com.project.hashnote.note.util.NoteEncrypter;
import com.project.hashnote.note.dao.NoteRepository;
import com.project.hashnote.note.document.Note;
import com.project.hashnote.note.mapper.NoteMapper;
import com.project.hashnote.exceptions.ResourceNotFoundException;
import com.project.hashnote.security.exception.UnauthorizedAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NoteServiceImpl implements NoteService {
    private NoteEncrypter noteEncrypter;
    private NoteRepository noteRepository;
    private NoteMapper noteMapper;
    private EncryptionMapper encryptionMapper;

    @Autowired
    public NoteServiceImpl(NoteEncrypter noteEncrypter, NoteRepository noteRepository,
                           NoteMapper noteMapper, EncryptionMapper encryptionMapper) {
        this.noteEncrypter = noteEncrypter;
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
        this.encryptionMapper = encryptionMapper;
    }

    @Override
    public EncryptionResponse save(NoteRequest noteRequest, String username) {
        return saveRequest(noteRequest, username);
    }

    private EncryptionResponse saveRequest(NoteRequest noteRequest, String username) {
        Note note = noteMapper.requestToNote(noteRequest, username);

        return processRequest(noteRequest, note);
    }

    private EncryptionResponse processRequest(NoteRequest noteRequest, Note note) {
        EncryptionResponse encryptionResponse;

        if (noteRequest.hasMethod()){
            EncryptionCredentials encryptionCredentials = noteEncrypter.encrypt(noteRequest);
            encryptionMapper.applyEncryption(encryptionCredentials, note);

            encryptionResponse = saveNote(encryptionCredentials, note);
        } else
            encryptionResponse = saveNote(note);

        return encryptionResponse;
    }

    private EncryptionResponse saveNote(EncryptionCredentials encryptionCredentials, Note note) {
        Note persistedNote = noteRepository.save(note);

        return new EncryptionResponse(persistedNote.getId(), new String(encryptionCredentials.getSecretKey()));
    }

    private EncryptionResponse saveNote(Note note) {
        Note persistedNote = noteRepository.save(note);

        return new EncryptionResponse(persistedNote.getId());
    }

    @Override
    public List<NoteDto> getAllBy(String username) {
        List<Note> notes = noteRepository.findByAuthor(username);

        return noteMapper.noteToNoteDtoList(notes);
    }

    @Override
    public NoteDto getOne(String id) {
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

        byte[] decryptedMessage = noteEncrypter.decrypt(note, secretKey);

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

    private void incrementNoteVisits(Note note) {
        note.setKeyVisits(note.getKeyVisits() + 1);

        noteRepository.save(note);
    }

    @Override
    public EncryptionResponse patch(NoteRequest patchRequest, String username, String id) {
        Note note = tryGetNoteForUser(username, id);

        if (note.isEncrypted())
            throw new UnauthorizedAccessException("This note requires a private key to be patched.");

        NoteRequest noteRequest = noteMapper.noteToRequest(note);

        noteMapper.copyProperties(patchRequest, noteRequest);

        if (!patchRequest.hasMethod())
            noteRequest.setMethod(null);

        note.setKeyVisits(0);

        return processRequest(noteRequest, note);
    }

    @Override
    public EncryptionResponse patch(NoteRequest patchRequest, String username, String id, String secretKey) {
        Note note = tryGetNoteForUser(username, id);

        NoteRequest noteRequest = getDecryptedRequest(note, secretKey);

        noteMapper.copyProperties(patchRequest, noteRequest);

        if (!patchRequest.hasMethod()) {
            noteRequest.setMethod(null);
            note.setEncryptionDetails(null);
        }

        note.setKeyVisits(0);

        return processRequest(noteRequest, note);
    }

    private NoteRequest getDecryptedRequest(Note note, String secretKey) {
        NoteRequest originalRequest = noteMapper.noteToRequest(note);

        byte[] message = noteEncrypter.decrypt(note, secretKey);
        originalRequest.setMessage(new String(message));

        return originalRequest;
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
