package com.project.hashnote.note.document;

import lombok.*;

@NoArgsConstructor
public @Data class EncryptionDetails {
    @NonNull
    private String vector;
    private String method;
}
