package com.project.hashnote.note.service;

import com.project.hashnote.note.dto.NoteDto;
import com.project.hashnote.note.dto.NoteRequest;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface NoteService {
    String save(NoteRequest noteRequest, UserDetails user);
    List<NoteDto> getAll();
    List<NoteDto> getAllBy(String username);
    NoteDto getEncrypted(String id);
    NoteDto getDecrypted(String id, String secretKey);
    String patch(String method, UserDetails user, String id, String secretKey);
    void delete(String id);
}
