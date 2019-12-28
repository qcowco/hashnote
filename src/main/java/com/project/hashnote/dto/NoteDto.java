package com.project.hashnote.dto;

import lombok.*;
import org.springframework.web.bind.annotation.RequestBody;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NoteDto {
    private String id;
    private String name;
    private String content;
    private String secretKey; // TODO: 28.12.2019 separate from this

    public NoteDto(String id, String name, String content) {
        this.id = id;
        this.name = name;
        this.content = content;
    }
}
