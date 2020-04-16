package com.project.hashnote.notefolder.mapper;

import com.project.hashnote.notefolder.document.Folder;
import com.project.hashnote.notefolder.dto.FolderDto;
import com.project.hashnote.notefolder.dto.FolderRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface FolderMapper {
    @Mapping(ignore = true, target = "id")
    Folder requestToFolder(FolderRequest request, String author, List notes);

    @Named("toDto")
    FolderDto folderToFolderDto(Folder folder);

    @IterableMapping(qualifiedByName = "toDto")
    List<FolderDto> folderToFolderDtoList(List<Folder> folders);
}
