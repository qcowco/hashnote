package com.project.hashnote.note.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EncryptionCredentials {
    private byte[] message;
    private byte[] secretKey;
    private byte[] vector;
    private String method;
}
