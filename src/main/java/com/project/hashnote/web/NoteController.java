package com.project.hashnote.web;

import com.project.hashnote.dto.NoteDto;
import com.project.hashnote.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
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
    public NoteDto getOneDecoded(@PathVariable(name = "id") String id, @PathVariable(name = "secretKey") String secretKey) {
        return noteService.getDecoded(id, secretKey);
    }

    @PostMapping // TODO: 28.12.2019 validation
    @ResponseStatus(HttpStatus.CREATED)
    public String save(@RequestBody NoteDto noteDto) {
        return noteService.save(noteDto);
    }

    @PutMapping
    public String put(@RequestBody NoteDto noteDto){
        // TODO: 28.12.2019 put
        return null;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable String id){
        // TODO: 28.12.2019 delete
        return null;
    }
}
