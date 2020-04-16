package com.project.hashnote.notefolder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@AllArgsConstructor
public @Data class FolderResponse extends RepresentationModel<FolderResponse> {
    private String folderId;
}
