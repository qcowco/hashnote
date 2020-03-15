package com.project.hashnote.user.document;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")
public @Data class User {
    private String id;
    private String username;
    private String password;
}
