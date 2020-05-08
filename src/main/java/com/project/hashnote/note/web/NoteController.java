package com.project.hashnote.note.web;

import com.project.hashnote.note.dto.EncryptionResponse;
import com.project.hashnote.note.dto.NoteDto;
import com.project.hashnote.note.dto.NoteRequest;
import com.project.hashnote.note.service.NoteService;
import com.project.hashnote.notefolder.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
        List<NoteDto> noteDtos = noteService.getAllBy(user.getUsername());

        noteDtos.forEach(this::linkEncryptedNoteAsSelf);

        return noteDtos;
    }

    private void linkEncryptedNoteAsSelf(NoteDto noteDto) {
        noteDto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder
                .methodOn(NoteController.class)
                .getOne(noteDto.getId()))
                .withSelfRel()
        );
    }

    @GetMapping("/{id}")
    public NoteDto getOne(@PathVariable String id){
        NoteDto noteDto = noteService.getOne(id);

        linkEncryptedNoteAsSelf(noteDto);

        return noteDto;
    }

    @GetMapping("/{id}/keys/{key}")
    public NoteDto getOneDecrypted(@PathVariable String id, @PathVariable String key) {
        NoteDto noteDto = noteService.getDecrypted(id, key);

        linkEncryptedNote(noteDto);
        linkDecryptedNoteAsSelf(noteDto, key);

        return noteDto;
    }

    private void linkEncryptedNote(NoteDto noteDto) {
        noteDto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder
                .methodOn(NoteController.class)
                .getOne(noteDto.getId()))
                .withRel("encrypted")
        );
    }

    private void linkDecryptedNoteAsSelf(NoteDto noteDto, String key) {
        noteDto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder
                .methodOn(NoteController.class)
                .getOneDecrypted(noteDto.getId(), key))
                .withSelfRel()
        );
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public EncryptionResponse save(@Valid @RequestBody NoteRequest noteRequest, @AuthenticationPrincipal UserDetails user) {
        String username;

        if (isUserLogged(user)) {
            username = user.getUsername();
        } else {
            username = "anon";

            if (exceedsFreeTimeLimit(noteRequest))
                noteRequest.setMinutesLeft(2880);
        }

        EncryptionResponse response = noteService.save(noteRequest, username);

        addLinks(response);

        return response;
    }

    private void addLinks(EncryptionResponse response) {
        if (response.hasSecretKey())
            linkDecryptedNote(response);
        linkEncryptedNoteAsSelf(response);
    }

    private void linkDecryptedNote(EncryptionResponse response) {
        response.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder
                .methodOn(NoteController.class)
                .getOneDecrypted(response.getNoteId(), response.getSecretKey()))
                .withRel("decrypted")
        );
    }

    private void linkEncryptedNoteAsSelf(EncryptionResponse response) {
        response.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder
                .methodOn(NoteController.class)
                .getOne(response.getNoteId()))
                .withSelfRel()
        );
    }

    private boolean exceedsFreeTimeLimit(@RequestBody @Valid NoteRequest noteRequest) {
        return noteRequest.getMinutesLeft() == 0 || noteRequest.getMinutesLeft() > 2880;
    }

    private boolean isUserLogged(UserDetails user) {
        return Objects.nonNull(user);
    }

    @PatchMapping("/{id}")
    public EncryptionResponse patch(@RequestBody NoteRequest noteRequest, @AuthenticationPrincipal UserDetails user,
                                    @PathVariable String id) {
        EncryptionResponse response = noteService.patch(noteRequest, user.getUsername(), id);

        addLinks(response);

        return response;
    }

    @PatchMapping("/{id}/keys/{secretKey}")
    public EncryptionResponse patch(@RequestBody NoteRequest noteRequest, @AuthenticationPrincipal UserDetails user,
                                    @PathVariable String id, @PathVariable String secretKey){
        EncryptionResponse response = noteService.patch(noteRequest, user.getUsername(), id, secretKey);

        addLinks(response);

        return response;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id, @AuthenticationPrincipal UserDetails userDetails){
        NoteDto note = noteService.getOne(id);

        noteService.delete(note.getId(), userDetails.getUsername());
        folderService.removeFromAll(note);
    }
}
