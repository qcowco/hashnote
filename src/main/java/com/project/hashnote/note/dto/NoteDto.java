package com.project.hashnote.note.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class NoteDto {
    @Null
    private String id;
    @NotNull
    @Length(max = 40)
    private String name;
    @NotNull
    @Length(min = 1, max = 20000)
    private String message;
}
