package com.project.hashnote.web;

import com.project.hashnote.dto.NoteDto;
import com.project.hashnote.dto.NoteRequest;
import com.project.hashnote.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notes")
public class NoteController {
    NoteService noteService;

    // TODO: 29.12.2019 kody 4xx dla InorrectPrivateKey, MalformedPrivateKey, InvalidAlgorithmName
    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping()
    public List<NoteDto> getAll(){
        return noteService.getAll();
    }

    @GetMapping("/{id}")
    public NoteDto getOne(@PathVariable String id){
        return noteService.getEncoded(id);
    }

    @GetMapping("/{id}/{secretKey}")
    public NoteDto getOneDecoded(@PathVariable(name = "id") String id, @PathVariable(name = "secretKey") String secretKey) { // TODO: 30.12.2019 validation keys
        return noteService.getDecrypted(id, secretKey);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE) // TODO: 28.12.2019 validation
    @ResponseStatus(HttpStatus.CREATED)
    public String save(@RequestBody NoteRequest noteRequest) {
        return noteService.save(noteRequest);
    }

    @PatchMapping("/{id}/{secretKey}")
    public void patch(@RequestBody NoteRequest noteRequest, @PathVariable String id, @PathVariable String secretKey){ // TODO: 30.12.2019 validation
        noteRequest.getNoteDto().setId(id);
        noteService.patch(noteRequest, secretKey);
    }

    @DeleteMapping("/{id}") // TODO: 30.12.2019 response code
    public void delete(@PathVariable String id){
        noteService.delete(id);
    }
}
