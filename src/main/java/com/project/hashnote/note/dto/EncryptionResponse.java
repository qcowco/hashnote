package com.project.hashnote.note.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@AllArgsConstructor
public @Data class EncryptionResponse extends RepresentationModel<EncryptionResponse> {
    private String noteId;
    private String secretKey;
}
