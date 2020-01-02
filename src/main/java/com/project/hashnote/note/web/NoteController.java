package com.project.hashnote.web;

import com.project.hashnote.note.dto.EncodingDetails;
import com.project.hashnote.note.dto.NoteDto;
import com.project.hashnote.note.dto.NoteRequest;
import com.project.hashnote.note.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notes")
public class NoteController {
    NoteService noteService;

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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE) // TODO: 28.12.2019 validation
    public String save(@RequestBody NoteRequest noteRequest) {
        return noteService.save(noteRequest);
    }

    @PatchMapping("/{id}/{secretKey}")
    public String patch(@RequestBody EncodingDetails encodingDetails, @PathVariable String id, @PathVariable String secretKey){ // TODO: 30.12.2019 validation
        return noteService.patch(encodingDetails, id, secretKey);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id){
        noteService.delete(id);
    }
}
