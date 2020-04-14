package com.project.hashnote.note.web;

import com.project.hashnote.encryption.exceptions.IncorrectPrivateKeyException;
import com.project.hashnote.encryption.exceptions.InvalidAlgorithmNameException;
import com.project.hashnote.encryption.exceptions.MalformedPrivateKeyException;
import com.project.hashnote.exceptions.ResourceNotFoundException;
import com.project.hashnote.note.exception.NoteExpiredException;
import com.project.hashnote.note.exception.UnlockLimitExceededException;
import com.project.hashnote.security.exception.UnauthorizedAccessException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class NoteExceptionController extends ResponseEntityExceptionHandler {

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                               HttpHeaders headers, HttpStatus status,
                                                               WebRequest request) {

        Map<String, Object> body = new HashMap<>();

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> x.getField() + ": " + x.getDefaultMessage())
                .collect(Collectors.toList());

        body.put("errors", errors);

        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public void handleUserNotFound(HttpServletResponse response, RuntimeException exception) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value(), exception.getMessage());
    }

    @ExceptionHandler({IncorrectPrivateKeyException.class, InvalidAlgorithmNameException.class,
            MalformedPrivateKeyException.class, IllegalArgumentException.class, InvalidKeyException.class})
    public void handleInvalidUserInput(HttpServletResponse response, RuntimeException exception) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    @ExceptionHandler({UnlockLimitExceededException.class, NoteExpiredException.class})
    public void handleForbiddenNoteAccess(HttpServletResponse response, RuntimeException exception) throws IOException {
        response.sendError(HttpStatus.FORBIDDEN.value(), exception.getMessage());
    }

}
