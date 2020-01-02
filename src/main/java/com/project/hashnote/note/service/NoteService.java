package com.project.hashnote.service;

import com.project.hashnote.note.dto.EncodingDetails;
import com.project.hashnote.note.dto.NoteDto;
import com.project.hashnote.note.dto.NoteRequest;

import java.util.List;

public interface NoteService {
    String save(NoteRequest noteRequest);
    List<NoteDto> getAll();
    NoteDto getEncoded(String id);
    NoteDto getDecrypted(String id, String secretKey);
    String patch(EncodingDetails encodingDetails, String id, String secretKey);
    void delete(String id);
}
