package com.project.hashnote.encoders.exceptions;

import java.io.IOException;

public class MalformedPrivateKeyException extends RuntimeException {
    public MalformedPrivateKeyException(String message) {
        super(message);
    }
}
