package com.project.hashnote.note.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@AllArgsConstructor
public @Data class EncryptionResponse extends RepresentationModel<EncryptionResponse> {
    @NonNull
    private String noteId;
    private String secretKey;

    public boolean hasSecretKey() {
        return StringUtils.hasText(secretKey);
    }
}
