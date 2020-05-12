package com.project.hashnote.notefolder.service;

import com.project.hashnote.note.dto.NoteDto;
import com.project.hashnote.notefolder.document.Folder;
import com.project.hashnote.notefolder.dto.FolderDto;
import com.project.hashnote.notefolder.dto.FolderRequest;
import com.project.hashnote.notefolder.dto.FolderResponse;

import java.util.List;

public interface FolderService {
    FolderResponse save(FolderRequest folderRequest, String author);
    List<FolderDto> getFoldersBy(String username);
    void delete(String folderId, String username);
    void patch(String folderId, String folderName, String username);
    void saveToFolder(NoteDto noteDto, String folderId, String username);
    void removeFromFolder(String noteId, String folderId, String username);
    void removeFromAll(NoteDto noteDto);
}
