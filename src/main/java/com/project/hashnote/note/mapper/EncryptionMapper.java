package com.project.hashnote.note.mapper;

import com.project.hashnote.note.document.Note;
import com.project.hashnote.note.dto.EncryptionDetails;
import com.project.hashnote.note.dto.NoteRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        uses = StringBytesMapper.class)
public interface EncryptionMapper {

    @Mapping(source = "noteDto.message", target = "message", qualifiedByName = "toBytes")
    @Mapping(ignore = true, target = "secretKey")
    @Mapping(ignore = true, target = "vector")
    EncryptionDetails getEncryptionDetails(NoteRequest request);

    @Mapping(source = "message", target = "message", qualifiedByName = "toString")
    @Mapping(source = "vector", target = "encryptionDetails.vector", qualifiedByName = "toString")
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "name")
    @Mapping(ignore = true, target = "author")
    void copyEncryptionDetails(EncryptionDetails source, @MappingTarget Note target);

    @Mapping(source = "note.message", target = "message", qualifiedByName = "toBytes")
    @Mapping(target = "secretKey", qualifiedByName = "toBytes")
    @Mapping(source = "note.encryptionDetails.vector", target = "vector", qualifiedByName = "toBytes")
    @Mapping(source = "note.encryptionDetails.method", target = "method")
    EncryptionDetails noteAndKeyToEncryption(Note note, String secretKey);
}
