package com.project.hashnote.note.service;

import com.project.hashnote.note.dto.NoteDto;
import com.project.hashnote.note.dto.NoteRequest;

import java.util.List;

public interface NoteService {
    String save(NoteRequest noteRequest);
    List<NoteDto> getAll();
    NoteDto getEncrypted(String id);
    NoteDto getDecrypted(String id, String secretKey);
    String patch(String method, String id, String secretKey);
    void delete(String id);
}
