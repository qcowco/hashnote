package com.project.hashnote.note.mapper;

import com.project.hashnote.note.document.Note;
import com.project.hashnote.note.dto.NoteDto;
import com.project.hashnote.note.dto.NoteRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = StringBytesMapper.class)
public interface NoteMapper {
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now(java.time.ZoneId.of(\"Europe/Warsaw\")))")
    @Mapping(target = "expiresAt",
            expression = "java(java.time.LocalDateTime.now(java.time.ZoneId.of(\"Europe/Warsaw\")).plusMinutes(noteRequest.getMinutesLeft()))")
    @Mapping(target = "encryptionDetails.method", source = "noteRequest.method")
    Note requestToNote(NoteRequest noteRequest, String author);

    @AfterMapping
    default void afterRequestMapping(@MappingTarget Note note, NoteRequest noteRequest, String author) {
        if (note.getCreatedAt().isEqual(note.getExpiresAt()) ||
                note.getExpiresAt().isBefore(note.getExpiresAt().plusSeconds(10))) {
            note.setExpiresAt(null);
        }
    }

    @InheritInverseConfiguration
    @Mapping(target = "minutesLeft",
            defaultExpression = "java(note.getExpiresAt().getMinutes() - note.getCreatedAt().getMinutes())")
    @Mapping(target = "method", source = "encryptionDetails.method")
    NoteRequest noteToRequest(Note note);

    @Named("toDto")
    NoteDto noteToNoteDto(Note note);

    @Named("toEntity")
    @InheritInverseConfiguration
    Note noteDtoToNote(NoteDto noteDto);

    @Mapping(source = "message", target = "message", qualifiedByName = "toString")
    NoteDto noteAndMessageToNoteDto(Note note, byte[] message);

    @IterableMapping(qualifiedByName = "toDto")
    List<NoteDto> noteToNoteDtoList(List<Note> notes);

    void copyProperties(NoteRequest source, @MappingTarget NoteRequest target);

    void copyNote(Note source, @MappingTarget Note target);

}
