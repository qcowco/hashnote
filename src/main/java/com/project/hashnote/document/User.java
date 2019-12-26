package com.project.hashnote.document;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document(collection = "users")
public class User {
    private String id;
    private String username;
    private String password;
    private List<String> notes;
    private List<String> folders;
}
