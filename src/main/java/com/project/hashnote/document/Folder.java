package com.project.hashnote.document;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document(collection = "folders")
public class Folder {
    private String id;
    private String name;
    private String ownerId;
    private List<String> notes;
}
