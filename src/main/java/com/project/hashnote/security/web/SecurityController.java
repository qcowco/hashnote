package com.project.hashnote.security.web;

import com.project.hashnote.security.dto.JwtRequest;
import com.project.hashnote.security.dto.JwtResponse;
import com.project.hashnote.security.service.SecurityService;
import com.project.hashnote.security.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class SecurityController {
    private SecurityService securityService;
    private AuthenticationManager authenticationManager;
    private JwtTokenUtil tokenUtil;

    @Autowired
    public SecurityController(SecurityService securityService,
                              AuthenticationManager authenticationManager, JwtTokenUtil tokenUtil) {
        this.securityService = securityService;
        this.authenticationManager = authenticationManager;
        this.tokenUtil = tokenUtil;
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestBody JwtRequest jwtRequest) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        jwtRequest.getUsername(),
                        jwtRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = tokenUtil.generateToken(jwtRequest.getUsername());

        return new JwtResponse(jwt);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public void register(@RequestBody JwtRequest jwtRequest) {
        securityService.save(jwtRequest);
    }
}
