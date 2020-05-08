package com.project.hashnote.note.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String secretKey;

    public boolean hasSecretKey() {
        return StringUtils.hasText(secretKey);
    }
}
