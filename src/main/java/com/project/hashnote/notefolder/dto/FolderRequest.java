package com.project.hashnote.notefolder.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

public @Data class FolderRequest {
    @NotNull
    private String name;
}
