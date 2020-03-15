package com.project.hashnote.user.document;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public @Data class User {
    private String id;
    @NonNull
    private String username;
    @NonNull
    private String password;
}
