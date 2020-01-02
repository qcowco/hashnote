package com.project.hashnote.encryption.exceptions;

public class MalformedVectorException extends RuntimeException {
    public MalformedVectorException(String message) {
        super(message);
    }
}
