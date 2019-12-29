package com.project.hashnote.dto.mapper;

import com.project.hashnote.document.EncodingDetails;
import com.project.hashnote.document.Note;
import com.project.hashnote.dto.NoteDto;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NoteMapper {
    @Named("fromDto")
    @Mapping(source = "dto.content", target = "content", qualifiedByName = "toBytes")
    @Mapping(source = "encodingDetails", target = "encodingDetails")
    @Mapping(ignore = true, target = "author")
    Note noteDtoToNote(NoteDto dto, EncodingDetails encodingDetails);

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
}
