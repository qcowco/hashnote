package com.project.hashnote.service;

import com.project.hashnote.dto.NoteDto;
import com.project.hashnote.dto.NoteRequest;

import java.util.List;

public interface NoteService {
    String save(NoteRequest noteRequest);
    List<NoteDto> getAll();
    NoteDto getEncoded(String id);
    NoteDto getDecrypted(String id, String secretKey);
    void patch(NoteRequest noteRequest, String key);
    void delete(String id);
}
