package com.project.hashnote.notefolder.document;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Document(collection = "folders")
public @Data class Folder {
    private String id;
    @NonNull
    private String name;
    @NonNull
    private String author;
    @NonNull
    private Map<String, String> noteIdName;
}
