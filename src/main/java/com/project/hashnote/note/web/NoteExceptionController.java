package com.project.hashnote.note.web;

import com.project.hashnote.encryption.exceptions.IncorrectPrivateKeyException;
import com.project.hashnote.encryption.exceptions.InvalidAlgorithmNameException;
import com.project.hashnote.encryption.exceptions.MalformedPrivateKeyException;
import com.project.hashnote.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidKeyException;


@ControllerAdvice
public class NoteExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public void handleUserNotFound(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler({IncorrectPrivateKeyException.class, InvalidAlgorithmNameException.class,
            MalformedPrivateKeyException.class, IllegalArgumentException.class, InvalidKeyException.class})
    public void handleInvalidUserInput(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

}
