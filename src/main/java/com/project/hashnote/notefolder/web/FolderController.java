package com.project.hashnote.notefolder.web;

import com.project.hashnote.notefolder.document.Folder;
import com.project.hashnote.notefolder.dto.FolderRequest;
import com.project.hashnote.notefolder.dto.FolderResponse;
import com.project.hashnote.notefolder.dto.NoteRequest;
import com.project.hashnote.notefolder.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public FolderResponse saveFolder(@RequestBody FolderRequest folderRequest,
                                     @AuthenticationPrincipal UserDetails user) {
        String folderId = folderService.save(folderRequest, user.getUsername());

        return new FolderResponse(folderId);
    }

    @DeleteMapping("/{folderId}")
    public void deleteFolder(@PathVariable String folderId,
                             @AuthenticationPrincipal UserDetails user) {
        folderService.delete(folderId, user.getUsername());
    }

    @PatchMapping("/{folderId}")
    public void patchFolder(@PathVariable String folderId, @RequestBody FolderRequest folderRequest,
                            @AuthenticationPrincipal UserDetails user) {
        folderService.patch(folderId, folderRequest.getName(), user.getUsername());
    }

    @PostMapping("/{folderId}")
    public void saveToFolder(@PathVariable String folderId, @RequestBody NoteRequest noteRequest,
                             @AuthenticationPrincipal UserDetails user) {
        folderService.saveToFolder(noteRequest.getNoteId(), folderId, user.getUsername());
    }

    @DeleteMapping("/{folderId}/notes/{noteId}")
    public void removeFromFolder(@PathVariable String noteId, @PathVariable String folderId,
                                 @AuthenticationPrincipal UserDetails user) {
        folderService.removeFromFolder(noteId, folderId, user.getUsername());
    }

}
