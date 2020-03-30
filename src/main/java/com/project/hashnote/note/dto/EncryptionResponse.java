package com.project.hashnote.note.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public @Data class EncryptionResponse {
    private String noteId;
    private String secretKey;
}
