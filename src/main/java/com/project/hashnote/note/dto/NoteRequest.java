package com.project.hashnote.note.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoteRequest {
    @NotNull
    @Valid
    private NoteDto noteDto;
    @NotNull
    private String method;
    @Min(0)
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

    public boolean hasMethod() {
        return StringUtils.hasText(method);
    }
}
