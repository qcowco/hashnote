package com.project.hashnote.notefolder.web;

import com.project.hashnote.note.dto.NoteDto;
import com.project.hashnote.note.service.NoteService;
import com.project.hashnote.note.web.NoteController;
import com.project.hashnote.notefolder.dto.FolderDto;
import com.project.hashnote.notefolder.dto.FolderRequest;
import com.project.hashnote.notefolder.dto.FolderResponse;
import com.project.hashnote.notefolder.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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
    private NoteService noteService;

    @Autowired
    public FolderController(FolderService folderService, NoteService noteService) {
        this.folderService = folderService;
        this.noteService = noteService;
    }

    @GetMapping
    public List<FolderDto> getFoldersBy(@AuthenticationPrincipal UserDetails user) {
        List<FolderDto> folderDtos = folderService.getFoldersBy(user.getUsername());

        folderDtos.forEach(folderDto -> folderDto.getNotes().forEach(this::linkEncrypted));

        return folderDtos;
    }

    private void linkEncrypted(NoteDto noteDto) {
        noteDto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder
                .methodOn(NoteController.class)
                .getOne(noteDto.getId()))
                .withRel("encrypted")
        );
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public FolderResponse saveFolder(@RequestBody FolderRequest folderRequest,
                                     @AuthenticationPrincipal UserDetails user) {
        FolderResponse folderResponse = folderService.save(folderRequest, user.getUsername());

        linkFolders(folderResponse, user);

        return folderResponse;
    }

    private void linkFolders(FolderResponse folderResponse, UserDetails user) {
        folderResponse.add(WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(FolderController.class)
                        .getFoldersBy(user))
                .withRel("folders")
        );
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

    @PostMapping("/{folderId}/notes/{noteId}")
    public void saveToFolder(@PathVariable String folderId, @PathVariable String noteId,
                             @AuthenticationPrincipal UserDetails user) {
        NoteDto noteDto = noteService.getOne(noteId);
        folderService.saveToFolder(noteDto, folderId, user.getUsername());
    }

    @DeleteMapping("/{folderId}/notes/{noteId}")
    public void removeFromFolder(@PathVariable String noteId, @PathVariable String folderId,
                                 @AuthenticationPrincipal UserDetails user) {
        folderService.removeFromFolder(noteId, folderId, user.getUsername());
    }

}
