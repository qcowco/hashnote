package com.project.hashnote.dto.mapper;

import com.project.hashnote.document.Note;
import com.project.hashnote.dto.NoteDto;
import com.project.hashnote.note.dto.NoteRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface NoteMapper {
    @Named("fromDto")
    @Mapping(source = "noteDto.id", target = "id")
    @Mapping(source = "noteDto.name", target = "name")
    @Mapping(source = "noteDto.content", target = "content", qualifiedByName = "toBytes")
    @Mapping(source = "encodingDetails.vector", target = "encodingDetails.vector")
    @Mapping(source = "encodingDetails.method", target = "encodingDetails.method")
    @Mapping(ignore = true, target = "author")
    Note requestToNote(NoteRequest noteRequest);

    @Mapping(source = "noteDto.id", target = "id")
    @Mapping(source = "noteDto.name", target = "name")
    @Mapping(source = "noteDto.content", target = "content", qualifiedByName = "toBytes")
    @Mapping(ignore = true, target = "author")
    @Mapping(ignore = true, target = "encodingDetails")
    Note noteDtoToNote(NoteDto noteDto);

    @Named("toBytes")
    default byte[] contentFromBase(String content){
        return content.getBytes();
    }

    @Named("toDto")
    @Mapping(source = "content", target = "content", qualifiedByName = "toString")
    NoteDto noteToNoteDto(Note note);

    @Named("toString")
    default String contentToBase(byte[] content){
        return new String(content);
    }

    @IterableMapping(qualifiedByName = "toDto")
    List<NoteDto> noteToNoteDtoList(List<Note> notes);

    void noteToNote(Note source, @MappingTarget Note target);
}
