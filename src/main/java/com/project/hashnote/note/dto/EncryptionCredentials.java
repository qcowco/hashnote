package com.project.hashnote.note.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
public @Data class EncryptionCredentials {
    private byte[] message;
    private byte[] secretKey;
    private byte[] vector;
    private String method;
}
