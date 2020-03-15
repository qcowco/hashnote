package com.project.hashnote.notefolder.service;

import com.project.hashnote.exceptions.ResourceNotFoundException;
import com.project.hashnote.note.dao.NoteRepository;
import com.project.hashnote.note.document.Note;
import com.project.hashnote.note.dto.NoteDto;
import com.project.hashnote.note.service.NoteService;
import com.project.hashnote.notefolder.dao.FolderRepository;
import com.project.hashnote.notefolder.document.Folder;
import com.project.hashnote.notefolder.dto.FolderRequest;
import com.project.hashnote.notefolder.mapper.FolderMapper;
import com.project.hashnote.security.service.SecurityService;
import com.project.hashnote.security.user.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FolderServiceImpl implements FolderService {
    private NoteService noteService;
    private SecurityService securityService;
    private FolderRepository folderRepository;
    private FolderMapper folderMapper;

    @Autowired
    public FolderServiceImpl(NoteService noteService, FolderRepository folderRepository,
                             FolderMapper folderMapper, SecurityService securityService) {
        this.noteService = noteService;
        this.securityService = securityService;
        this.folderRepository = folderRepository;
        this.folderMapper = folderMapper;
    }

    @Override
    public String save(FolderRequest folderRequest, String author) {
        tryFindUser(author);

        Folder folder = folderMapper.requestToFolder(folderRequest, author, new HashMap<String, String>());

        return folderRepository.save(folder).getId();
    }

    private void tryFindUser(String username) {
        if (!securityService.userExists(username))
            throw new UsernameNotFoundException("No user found with id: " + username);
    }

    @Override
    public List<Folder> getFoldersBy(String username) {
        return folderRepository.findByAuthor(username);
    }

    @Override
    public void delete(String folderId) {
        Folder folder = tryFindFolder(folderId);

        folderRepository.delete(folder);
    }

    private Folder tryFindFolder(String folderId) {
        return folderRepository.findById(folderId)
                .orElseThrow(() -> new ResourceNotFoundException("No folder found with id: " + folderId));
    }

    @Override
    public void patch(String folderId, String folderName) {
        Folder folder = tryFindFolder(folderId);
        folder.setName(folderName);

        folderRepository.save(folder);
    }

    @Override
    public void saveToFolder(String noteId, String folderId) {
        Folder folder = tryFindFolder(folderId);

        Map<String, String> notes = folder.getNoteIdName();

        if (notes.containsKey(noteId))
            throw new IllegalArgumentException("Note already exists in this folder");

        NoteDto note = noteService.getEncrypted(noteId);
        notes.put(note.getId(), note.getName());

        folderRepository.save(folder);
    }

    @Override
    public void removeFromFolder(String noteId, String folderId) {
        Folder folder = tryFindFolder(folderId);

        Map<String, String> notes = folder.getNoteIdName();

        if (!notes.containsKey(noteId))
            throw new IllegalArgumentException("Note doesn't exist in this folder");

        notes.remove(noteId);

        folderRepository.save(folder);
    }
}
