package com.project.hashnote.note.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class NoteDto {
    private String id;
    private String name;
    private String content;
}
