package com.project.hashnote.note.exception;

public class UnlockLimitExceededException extends RuntimeException {
    public UnlockLimitExceededException(String message) {
        super(message);
    }
}
