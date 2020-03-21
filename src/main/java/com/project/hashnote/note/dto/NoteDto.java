package com.project.hashnote.note.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

public @Data class NoteDto {
    @Null
    private String id;
    @NotNull
    @Length(max = 40)
    private String name;
    @NotNull
    @Length(min = 1, max = 20000)
    private String message;
}
