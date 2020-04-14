package com.project.hashnote.note.exception;

public class NoteExpiredException extends RuntimeException {
    public NoteExpiredException(String message) {
        super(message);
    }
}
