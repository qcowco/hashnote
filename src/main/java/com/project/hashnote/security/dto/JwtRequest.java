package com.project.hashnote.security.dto;

import lombok.Data;

public @Data class JwtRequest {
    private String username;
    private String password;
}
