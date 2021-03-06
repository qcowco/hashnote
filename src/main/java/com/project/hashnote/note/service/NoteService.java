package com.project.hashnote.note.service;

import com.project.hashnote.note.dto.EncryptionResponse;
import com.project.hashnote.note.dto.NoteDto;
import com.project.hashnote.note.dto.NoteRequest;

import java.util.List;

public interface NoteService {
    EncryptionResponse save(NoteRequest noteRequest, String username);
    List<NoteDto> getAllBy(String username);
    NoteDto getOne(String id);
    NoteDto getDecrypted(String id, String secretKey);
    EncryptionResponse patch(NoteRequest noteRequest, String username, String id);
    EncryptionResponse patch(NoteRequest noteRequest, String username, String id, String secretKey);
    void delete(String id, String username);
    void delete(String id);
    List<NoteDto> findExpired();
    List<NoteDto> findLimited();
}
