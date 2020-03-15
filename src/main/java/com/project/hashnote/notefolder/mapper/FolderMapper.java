package com.project.hashnote.notefolder.mapper;

import com.project.hashnote.notefolder.document.Folder;
import com.project.hashnote.notefolder.dto.FolderRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Map;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface FolderMapper {
    @Mapping(ignore = true, target = "id")
    Folder requestToFolder(FolderRequest request, String author, Map noteIdName);
}
