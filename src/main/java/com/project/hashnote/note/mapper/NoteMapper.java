package com.project.hashnote.note.mapper;

import com.project.hashnote.note.document.Note;
import com.project.hashnote.note.dto.NoteDto;
import com.project.hashnote.note.dto.NoteRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        uses = StringBytesMapper.class)
public interface NoteMapper {
    @Named("fromDto")
    @Mapping(source = "noteDto.id", target = "id")
    @Mapping(source = "noteDto.name", target = "name")
    @Mapping(source = "noteDto.content", target = "content", qualifiedByName = "toBytes")
    @Mapping(source = "method", target = "encryptionDetails.method")
    @Mapping(ignore = true, target = "author")
    Note requestToNote(NoteRequest noteRequest);

    @Named("toDto")
    @Mapping(source = "content", target = "content", qualifiedByName = "toString")
    NoteDto noteToNoteDto(Note note);

    @IterableMapping(qualifiedByName = "toDto")
    List<NoteDto> noteToNoteDtoList(List<Note> notes);
}
