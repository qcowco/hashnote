package com.project.hashnote.security.web;

import com.project.hashnote.security.exception.UnauthorizedAccessException;
import com.project.hashnote.security.exception.UserExistsException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.MissingClaimException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class SecurityExceptionController {

    @ExceptionHandler({UserExistsException.class})
    public void handleRegisterUser(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler({MalformedJwtException.class, MissingClaimException.class, ExpiredJwtException.class})
    public void handleInvalidJWT(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public void handleUnauthorizedAccess(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.UNAUTHORIZED.value());
    }
}
