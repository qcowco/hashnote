package com.project.hashnote.notefolder.service;

import com.project.hashnote.notefolder.document.Folder;
import com.project.hashnote.notefolder.dto.FolderRequest;

import java.util.List;

public interface FolderService {
    String save(FolderRequest folderRequest, String author);
    List<Folder> getFoldersBy(String username);
    void delete(String folderId);
    void patch(String folderId, String folderName);
    void saveToFolder(String noteId, String folderId);
    void removeFromFolder(String noteId, String folderId);
}
