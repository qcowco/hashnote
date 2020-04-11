package com.project.hashnote.note.web;

import com.project.hashnote.note.dto.EncryptionResponse;
import com.project.hashnote.note.dto.NoteDto;
import com.project.hashnote.note.dto.NoteRequest;
import com.project.hashnote.note.service.NoteService;
import com.project.hashnote.notefolder.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/notes")
@CrossOrigin
public class NoteController {
    private NoteService noteService;
    private FolderService folderService;

    @Autowired
    public NoteController(NoteService noteService, FolderService folderService) {
        this.noteService = noteService;
        this.folderService = folderService;
    }

    @GetMapping
    public List<NoteDto> getAll(@AuthenticationPrincipal UserDetails user){
        return noteService.getAllBy(user.getUsername());
    }

    @GetMapping("/{id}")
    public NoteDto getOne(@PathVariable String id){
        return noteService.getEncrypted(id);
    }

    @GetMapping("/{id}/keys/{key}")
    public NoteDto getOneDecrypted(@PathVariable String id, @PathVariable String key) {
        return noteService.getDecrypted(id, key);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public EncryptionResponse save(@Valid @RequestBody NoteRequest noteRequest, @AuthenticationPrincipal UserDetails user) {
        String username;

        if (isUserLogged(user)) {
            username = user.getUsername();
        } else {
            username = "anon";

            if (noteRequest.getMinutesToExpiration() == 0 || noteRequest.getMinutesToExpiration() > 2880)
                noteRequest.setMinutesToExpiration(2880);
        }

        return noteService.save(noteRequest, username);
    }

    private boolean isUserLogged(UserDetails user) {
        return Objects.nonNull(user);
    }

    @PatchMapping("/{id}/keys/{secretKey}")
    public EncryptionResponse patch(@Valid @RequestBody NoteRequest noteRequest, @AuthenticationPrincipal UserDetails user,
                        @PathVariable String id, @PathVariable String secretKey){
        return noteService.patch(noteRequest, user.getUsername(), id, secretKey);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id, @AuthenticationPrincipal UserDetails userDetails){
        NoteDto note = noteService.getEncrypted(id);

        noteService.delete(note.getId(), userDetails.getUsername());
        folderService.removeFromAll(note);
    }
}
