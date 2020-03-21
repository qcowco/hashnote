package com.project.hashnote.security.web;

import com.project.hashnote.security.exception.UnauthorizedAccessException;
import com.project.hashnote.security.exception.UserExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class SecurityExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler({UserExistsException.class})
    public void handleRegisterUser(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public void handleLoginUser(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public void handleUnauthorizedAccess(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.UNAUTHORIZED.value());
    }
}
