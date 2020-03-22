package com.project.hashnote.note.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

public @Data class NoteDto {
    @Null
    private String id;
    @NotNull
    @Length(max = 40)
    private String name;
    @NotNull
    @Length(min = 1, max = 20000)
    private String message;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime expiresAt;
}
