package com.project.hashnote.note.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoteRequest {
    private NoteDto noteDto;
    private String method;

    public String getId() {
        return noteDto.getId();
    }

    public String getName() {
        return noteDto.getName();
    }

    public String getContent() {
        return noteDto.getContent();
    }
}
