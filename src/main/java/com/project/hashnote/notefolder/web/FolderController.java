package com.project.hashnote.notefolder.web;

import com.project.hashnote.note.dao.NoteRepository;
import com.project.hashnote.note.document.Note;
import com.project.hashnote.notefolder.dao.FolderRepository;
import com.project.hashnote.notefolder.document.Folder;
import com.project.hashnote.notefolder.dto.FolderRequest;
import com.project.hashnote.notefolder.dto.FolderResponse;
import com.project.hashnote.notefolder.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/folders")
public class FolderController {
    private FolderService folderService;

    @Autowired
    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @GetMapping
    public List<Folder> getFoldersBy(@AuthenticationPrincipal UserDetails user) {
        return folderService.getFoldersBy(user.getUsername());
    }

    @PostMapping
    public FolderResponse saveFolder(@RequestBody FolderRequest folderRequest, @AuthenticationPrincipal UserDetails user) {
        String folderId = folderService.save(folderRequest, user.getUsername());
// TODO: 14.03.2020 name validation 32 max length


        return new FolderResponse(folderId);
    }

    @DeleteMapping("/{folderId}")
    public void deleteFolder(@PathVariable String folderId) {
        folderService.delete(folderId);
    }

    @PatchMapping("/{folderId}")
    public void patchFolder(@PathVariable String folderId, @RequestBody FolderRequest folderRequest) {
        folderService.patch(folderId, folderRequest.getName());
    }

    @PostMapping("/{folderId}/notes/{noteId}")
    public void saveToFolder(@PathVariable String noteId, @PathVariable String folderId) {
        folderService.saveToFolder(noteId, folderId);
    }

    @DeleteMapping("/{folderId}/notes/{noteId}")
    public void removeFromFolder(@PathVariable String noteId, @PathVariable String folderId) {
        folderService.removeFromFolder(noteId, folderId);
    }

}
