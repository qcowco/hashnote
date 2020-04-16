package com.project.hashnote.notefolder.service;

import com.project.hashnote.exceptions.ResourceNotFoundException;
import com.project.hashnote.note.dto.NoteDto;
import com.project.hashnote.notefolder.dao.FolderRepository;
import com.project.hashnote.notefolder.document.Folder;
import com.project.hashnote.notefolder.dto.FolderDto;
import com.project.hashnote.notefolder.dto.FolderRequest;
import com.project.hashnote.notefolder.mapper.FolderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FolderServiceImpl implements FolderService {
    private FolderRepository folderRepository;
    private FolderMapper folderMapper;

    @Autowired
    public FolderServiceImpl(FolderRepository folderRepository, FolderMapper folderMapper) {
        this.folderRepository = folderRepository;
        this.folderMapper = folderMapper;
    }

    @Override
    public String save(FolderRequest folderRequest, String author) {
        Folder folder = folderMapper.requestToFolder(folderRequest, author, new LinkedList<NoteDto>());

        return folderRepository.save(folder).getId();
    }

    @Override
    public List<FolderDto> getFoldersBy(String username) {
        List<Folder> folders = folderRepository.findByAuthor(username);

        return folderMapper.folderToFolderDtoList(folders);
    }

    @Override
    public void delete(String folderId, String username) {
        Folder folder = getFolderBy(username, folderId);

        folderRepository.delete(folder);
    }

    private Folder getFolderBy(String username, String folderId) {
        List<Folder> usersFolders = folderRepository.findByAuthor(username);

        Optional<Folder> optionalFolder = usersFolders.stream()
                .filter(folder -> folder.getId().equals(folderId))
                .findFirst();

        return optionalFolder
                .orElseThrow(() -> new ResourceNotFoundException("There's no such folder for this user"));
    }

    @Override
    public void patch(String folderId, String folderName, String username) {
        Folder folder = getFolderBy(username, folderId);
        folder.setName(folderName);

        folderRepository.save(folder);
    }

    @Override
    public void saveToFolder(NoteDto noteDto, String folderId, String username) {
        Folder folder = getFolderBy(username, folderId);

        List<NoteDto> notes = folder.getNotes();

        if (notes.stream().anyMatch(note -> noteDto.getId().equals(note.getId())))
            throw new IllegalArgumentException("Note already exists in this folder");

        noteDto.setMessage("");
        notes.add(noteDto);

        folderRepository.save(folder);
    }

    @Override
    public void removeFromFolder(String noteId, String folderId, String username) {
        Folder folder = getFolderBy(username, folderId);

        List<NoteDto> notes = folder.getNotes();

        NoteDto noteDto = notes.stream().filter(note -> noteId.equals(note.getId())).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Note doesn't exist in this folder"));


        notes.remove(noteDto);

        folderRepository.save(folder);
    }

    @Override
    public void removeFromAll(NoteDto noteDto) {
        noteDto.setMessage("");
        List<Folder> folders = folderRepository.findByNotesContaining(noteDto);
        folders.forEach(folder -> {
                    boolean removed = folder.getNotes()
                            .removeIf(note -> noteDto.getId().equals(note.getId()));

                    if (removed)
                        folderRepository.save(folder);
                });
    }


}
