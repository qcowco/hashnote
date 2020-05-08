package com.project.hashnote.note.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

public @Data class NoteDto extends RepresentationModel<NoteDto> {
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime expiresAt;
    private int keyVisits;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int maxVisits;
}
