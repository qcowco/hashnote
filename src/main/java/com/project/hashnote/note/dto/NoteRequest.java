package com.project.hashnote.note.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoteRequest {
    @Valid
    private NoteDto noteDto;
    private String method;
    private int minutesToExpiration;

    public String getId() {
        return noteDto.getId();
    }

    public String getMessage() {
        return noteDto.getMessage();
    }

    public boolean hasNoteId() {
        return noteDto.getId() != null;
    }
}
