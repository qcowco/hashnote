package com.project.hashnote.security.service;

import com.project.hashnote.security.dto.JwtRequest;
import com.project.hashnote.security.exception.UserExistsException;
import com.project.hashnote.user.dao.UserRepository;
import com.project.hashnote.user.document.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SecurityServiceImpl {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void save(JwtRequest request) {
        String username = request.getUsername();
        String password = passwordEncoder.encode(request.getPassword());

        if (userExists(username))
            throw new UserExistsException("This username is already taken: " + username);

        userRepository.save(new User(username, password));

    }

    private boolean userExists(String username) {
        return userRepository.findByUsername(username)
                .isPresent();
    }
}
