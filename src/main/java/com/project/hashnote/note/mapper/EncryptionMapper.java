package com.project.hashnote.note.mapper;

import com.project.hashnote.note.document.Note;
import com.project.hashnote.note.dto.EncryptionCredentials;
import com.project.hashnote.note.dto.NoteRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        uses = StringBytesMapper.class)
public interface EncryptionMapper {

    @Mapping(source = "message", target = "message", qualifiedByName = "toBytes")
    @Mapping(ignore = true, target = "secretKey")
    @Mapping(ignore = true, target = "vector")
    EncryptionCredentials getEncryptionDetails(NoteRequest request);

    @Mapping(source = "message", target = "message", qualifiedByName = "toString")
    @Mapping(source = "vector", target = "encryptionDetails.vector", qualifiedByName = "toString")
    @Mapping(source = "method", target = "encryptionDetails.method")
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "name")
    @Mapping(ignore = true, target = "author")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "expiresAt")
    @Mapping(ignore = true, target = "keyVisits")
    @Mapping(ignore = true, target = "maxVisits")
    void applyEncryption(EncryptionCredentials source, @MappingTarget Note target);

    @Mapping(source = "note.message", target = "message", qualifiedByName = "toBytes")
    @Mapping(target = "secretKey", qualifiedByName = "toBytes")
    @Mapping(source = "note.encryptionDetails.vector", target = "vector", qualifiedByName = "toBytes")
    @Mapping(source = "note.encryptionDetails.method", target = "method")
    EncryptionCredentials noteAndKeyToEncryption(Note note, String secretKey);
}
