package com.project.hashnote.notefolder.service;

import com.project.hashnote.exceptions.ResourceNotFoundException;
import com.project.hashnote.note.dto.NoteDto;
import com.project.hashnote.note.service.NoteService;
import com.project.hashnote.notefolder.dao.FolderRepository;
import com.project.hashnote.notefolder.document.Folder;
import com.project.hashnote.notefolder.dto.FolderRequest;
import com.project.hashnote.notefolder.mapper.FolderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FolderServiceImpl implements FolderService {
    private NoteService noteService;
    private FolderRepository folderRepository;
    private FolderMapper folderMapper;

    @Autowired
    public FolderServiceImpl(NoteService noteService, FolderRepository folderRepository,
                             FolderMapper folderMapper) {
        this.noteService = noteService;
        this.folderRepository = folderRepository;
        this.folderMapper = folderMapper;
    }

    @Override
    public String save(FolderRequest folderRequest, String author) {
        Folder folder = folderMapper.requestToFolder(folderRequest, author, new LinkedList<NoteDto>());

        return folderRepository.save(folder).getId();
    }

    @Override
    public List<Folder> getFoldersBy(String username) {
        return folderRepository.findByAuthor(username);
    }

    @Override
    public void delete(String folderId, String username) {
        Folder folder = findFolderBy(username, folderId);

        folderRepository.delete(folder);
    }

    private Folder findFolderBy(String username, String folderId) {
        List<Folder> usersFolders = getFoldersBy(username);

        Optional<Folder> optionalFolder = usersFolders.stream()
                .filter(folder -> folder.getId().equals(folderId))
                .findFirst();

        return optionalFolder
                .orElseThrow(() -> new ResourceNotFoundException("There's no such folder for this user"));
    }

    @Override
    public void patch(String folderId, String folderName, String username) {
        Folder folder = findFolderBy(username, folderId);
        folder.setName(folderName);

        folderRepository.save(folder);
    }

    @Override
    public void saveToFolder(String noteId, String folderId, String username) {
        Folder folder = findFolderBy(username, folderId);

        List<NoteDto> notes = folder.getNotes();

        if (notes.stream().anyMatch(note -> noteId.equals(note.getId())))
            throw new IllegalArgumentException("Note already exists in this folder");

        NoteDto note = noteService.getEncrypted(noteId);
        note.setMessage("");

        notes.add(note);

        folderRepository.save(folder);
    }

    @Override
    public void removeFromFolder(String noteId, String folderId, String username) {
        Folder folder = findFolderBy(username, folderId);

        List<NoteDto> notes = folder.getNotes();

        NoteDto noteDto = notes.stream().filter(note -> noteId.equals(note.getId())).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Note doesn't exist in this folder"));

        notes.remove(noteDto);

        folderRepository.save(folder);
    }
}
