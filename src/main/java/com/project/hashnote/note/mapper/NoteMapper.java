package com.project.hashnote.note.mapper;

import com.project.hashnote.note.document.Note;
import com.project.hashnote.note.dto.NoteDto;
import com.project.hashnote.note.dto.NoteRequest;
import jdk.jfr.Name;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = StringBytesMapper.class)
public interface NoteMapper {
    @Named("fromDto")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "message", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "encryptionDetails.method", source = "noteRequest.method")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now(java.time.ZoneId.of(\"Europe/Warsaw\")))")
    @Mapping(target = "expiresAt",
            expression = "java(java.time.LocalDateTime.now(java.time.ZoneId.of(\"Europe/Warsaw\")).plusMinutes(noteRequest.getMinutesToExpiration()))")
    @Mapping(target = "keyVisits", expression = "java(0)")
    Note requestToNote(NoteRequest noteRequest);

    @AfterMapping
    default void afterRequestMapping(@MappingTarget Note note, NoteRequest noteRequest) {
        if (note.getCreatedAt().isEqual(note.getExpiresAt())) {
            note.setExpiresAt(null);
        }
    }

    @InheritInverseConfiguration
    @Mapping(target = "noteDto", source = "note", qualifiedByName = "toDto")
    @Mapping(target = "minutesToExpiration",
            defaultExpression = "java(note.getExpiresAt().getMinutes() - note.getCreatedAt().getMinutes())")
    @Mapping(target = "method", source = "encryptionDetails.method")
    NoteRequest noteToRequest(Note note);

    @Named("toDto")
    NoteDto noteToNoteDto(Note note);

    @Name("toEntity")
    @InheritInverseConfiguration
    Note noteDtoToNote(NoteDto noteDto);

    @Mapping(source = "message", target = "message", qualifiedByName = "toString")
    NoteDto noteAndMessageToNoteDto(Note note, byte[] message);

    @IterableMapping(qualifiedByName = "toDto")
    List<NoteDto> noteToNoteDtoList(List<Note> notes);

    @Mapping(ignore = true, target = "target.noteDto")
    @Mapping(source = "source.method", target = "target.method")
    @Mapping(source = "source.minutesToExpiration", target = "target.minutesToExpiration")
    void copyProperties(NoteRequest source, @MappingTarget NoteRequest target);

    void copyNote(Note source, @MappingTarget Note target);

}
