package com.project.hashnote.note.task;

import com.project.hashnote.note.dto.NoteDto;
import com.project.hashnote.note.service.NoteService;
import com.project.hashnote.notefolder.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteTasks {
    private NoteService noteService;
    private FolderService folderService;

    @Autowired
    public NoteTasks(NoteService noteService, FolderService folderService) {
        this.noteService = noteService;
        this.folderService = folderService;
    }

    @Scheduled(cron = "${note.deleteSchedule.cron}")
    public void deleteExpiredNotes() {
        List<NoteDto> expiredNotes = noteService.findExpired();

        System.out.println("usuwam " + expiredNotes);
        for (NoteDto note: expiredNotes) {
            noteService.delete(note.getId());
            folderService.removeFromAll(note);
        }
    }
}
