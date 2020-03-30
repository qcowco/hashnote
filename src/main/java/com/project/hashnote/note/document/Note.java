package com.project.hashnote.note.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@NoArgsConstructor
@Document(collection = "notes")
public @Data class Note {
    @Id
    private String id;
    private String name;
    private String message;
    private EncryptionDetails encryptionDetails;
    private String author;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private int keyVisits;
    private int maxVisits;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return id.equals(note.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
