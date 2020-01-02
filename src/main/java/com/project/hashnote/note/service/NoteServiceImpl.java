package com.project.hashnote.service;

import com.project.hashnote.dao.NoteRepository;
import com.project.hashnote.document.Note;
import com.project.hashnote.note.dto.EncodingDetails;
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
    private NoteRepository noteRepository;
    private NoteMapper noteMapper;

    @Autowired
    public NoteServiceImpl(NoteRepository noteRepository, NoteMapper noteMapper, NoteEncoder noteEncoder) {
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
        this.noteEncoder = noteEncoder;
    }

    @Override
    public String save(NoteRequest noteRequest) {
        noteRequest.getNoteDto().setId(null);

        NoteRequest encodedRequest = noteEncoder.encodeRequest(noteRequest);

        Note note = noteMapper.requestToNote(encodedRequest);
        Note persistedNote = noteRepository.save(note);

        EncodingDetails encodingDetails = encodedRequest.getEncodingDetails();

        return persistedNote.getId() + "/" + encodingDetails.getKey();
    }

    @Override
    public List<NoteDto> getAll() {
        List<Note> notes = noteRepository.findAll();

        return noteMapper.noteToNoteDtoList(notes);
    }

    @Override
    public NoteDto getEncoded(String id) {
        Note note = tryGetNoteById(id);

        return noteMapper.noteToNoteDto(note);
    }

    @Override
    public NoteDto getDecrypted(String id, String secretKey) {
        Note note = tryGetNoteById(id);

        byte[] decryptedMessage = noteEncoder.decrypt(note, secretKey);

        note.setContent(decryptedMessage);

        return noteMapper.noteToNoteDto(note);
    }


    private Note tryGetNoteById(String id) {
        Optional<Note> optionalNote = noteRepository.findById(id);

        return optionalNote.orElseThrow(
                () -> new ResourceNotFoundException("No note found with id: " + id)
        );
    }

    @Override
    public String patch(EncodingDetails encodingDetails, String id, String secretKey) {
        NoteDto decryptedDto = getDecrypted(id, secretKey);

        NoteRequest noteRequest = new NoteRequest();
        noteRequest.setNoteDto(decryptedDto);
        noteRequest.setEncodingDetails(encodingDetails);

        NoteRequest encodedRequest = noteEncoder.encodeRequest(noteRequest);
        EncodingDetails encodingResult = encodedRequest.getEncodingDetails();

        Note note = noteMapper.requestToNote(encodedRequest);
        Note persistedNote = noteRepository.save(note);

        return persistedNote.getId() + "/" + encodingResult.getKey();
    }

    @Override
    public void delete(String id) {
        noteRepository.deleteById(id);
    }
}