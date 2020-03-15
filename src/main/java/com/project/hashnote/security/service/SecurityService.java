package com.project.hashnote.security.service;

import com.project.hashnote.security.dto.JwtRequest;

public interface SecurityService {
    void save(JwtRequest request);
}
