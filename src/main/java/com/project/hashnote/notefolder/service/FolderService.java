package com.project.hashnote.notefolder.service;

import com.project.hashnote.note.dto.NoteDto;
import com.project.hashnote.notefolder.document.Folder;
import com.project.hashnote.notefolder.dto.FolderRequest;

import java.util.List;

public interface FolderService {
    String save(FolderRequest folderRequest, String author);
    List<Folder> getFoldersBy(String username);
    void delete(String folderId, String username);
    void patch(String folderId, String folderName, String username);
    void saveToFolder(String noteId, String folderId, String username);
    void removeFromFolder(String noteId, String folderId, String username);
    void removeFromAll(NoteDto noteDto);
}
