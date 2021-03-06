package com.project.hashnote.notefolder.document;

import com.project.hashnote.note.dto.NoteDto;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@NoArgsConstructor
@Document(collection = "folders")
public @Data class Folder {
    private String id;
    @NonNull
    private String name;
    @NonNull
    private String author;
    @NonNull
    private List<NoteDto> notes;
}
