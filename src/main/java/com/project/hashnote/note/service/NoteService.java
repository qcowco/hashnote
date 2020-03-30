package com.project.hashnote.note.service;

import com.project.hashnote.note.dto.NoteDto;
import com.project.hashnote.note.dto.NoteRequest;

import java.util.List;

public interface NoteService {
    String save(NoteRequest noteRequest, String username);
    List<NoteDto> getAll();
    List<NoteDto> getAllBy(String username);
    NoteDto getEncrypted(String id);
    NoteDto getDecrypted(String id, String secretKey);
    String patch(String method, String username, String id, String secretKey);
    void delete(String id);
    List<NoteDto> findExpired();
    List<NoteDto> findLimited();
}
