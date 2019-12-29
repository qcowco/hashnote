package com.project.hashnote.encoders.exceptions;

import java.io.IOException;

public class IncorrectPrivateKeyException extends IOException {
    public IncorrectPrivateKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
