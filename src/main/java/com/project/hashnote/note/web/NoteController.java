package com.project.hashnote.note.web;

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
import java.util.List;
import java.util.Map;

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

    @GetMapping("/{id}/{secretKey}")
    public NoteDto getOneDecrypted(@PathVariable String id, @PathVariable String secretKey) { // TODO: 30.12.2019 validation keys
        return noteService.getDecrypted(id, secretKey);
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String save(@Valid @RequestBody NoteRequest noteRequest, @AuthenticationPrincipal UserDetails user) {

        String username = "anon";
        if (user != null)
            username = user.getUsername();

        return noteService.save(noteRequest, username);
    }

    @PatchMapping("/{id}/{secretKey}")
    public String patch(@RequestBody Map<String, String> jsonMap, @AuthenticationPrincipal UserDetails user,
                        @PathVariable String id, @PathVariable String secretKey){
        String method = tryGetKey(jsonMap, "method");

        return noteService.patch(method, user.getUsername(), id, secretKey);
    }

    private String tryGetKey(Map<String, String> jsonBody, String key) {
        if(!jsonBody.containsKey(key))
            throw new IllegalArgumentException("Missing argument: " + key);

        return jsonBody.get(key);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id){
        NoteDto note = noteService.getEncrypted(id);

        noteService.delete(note.getId());
        folderService.removeFromAll(note);
    }
}
// TODO: 21.03.2020 autoryzacja delete
