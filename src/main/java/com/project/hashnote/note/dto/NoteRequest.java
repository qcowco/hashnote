package com.project.hashnote.note.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoteRequest {
    @Valid
    private NoteDto noteDto;
    private String method;
    private int minutesToExpiration;
    @Min(0)
    private int maxVisits;

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
