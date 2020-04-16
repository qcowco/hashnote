package com.project.hashnote.notefolder.dto;

import com.project.hashnote.note.dto.NoteDto;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

public @Data class FolderDto extends RepresentationModel<FolderDto> {
    private String id;
    private String name;
    private String author;
    private List<NoteDto> notes;
}
