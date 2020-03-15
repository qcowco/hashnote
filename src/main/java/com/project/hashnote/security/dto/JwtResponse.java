package com.project.hashnote.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public @Data class JwtResponse {
    private String token;
}
