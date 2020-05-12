package com.project.hashnote.security.service;

import com.project.hashnote.security.dto.JwtRequest;
import com.project.hashnote.security.exception.UserExistsException;
import com.project.hashnote.security.user.dao.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityServiceImplTest {
    private final String USERNAME = "USERNAME";
    private final String PASSWORD = "PASSWORD";

    @InjectMocks
    private SecurityServiceImpl securityService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @DisplayName("Given an account is being created")
    @Nested
    class CreateAccount {

        @DisplayName("When username isn't available")
        @Nested
        class UsernameNotAvailable {

            @DisplayName("Then throws UserExistsException")
            @Test
            public void shouldThrowUserExistsException() {
                //Given
                JwtRequest jwtRequest = new JwtRequest();
                jwtRequest.setUsername(USERNAME);
                jwtRequest.setPassword(PASSWORD);

                boolean userExists = true;

                when(passwordEncoder.encode(PASSWORD))
                        .thenReturn(PASSWORD);

                when(userRepository.existsByUsername(USERNAME))
                        .thenReturn(userExists);

                //When/Then
                assertThrows(UserExistsException.class, () -> securityService.save(jwtRequest));
            }
        }
    }

    @DisplayName("Given user existance is being checked")
    @Nested
    class DoesUserExist {

        @DisplayName("Then returns whether username is taken")
        @Test
        public void shouldReturnIfUsernameIsTaken() {
            //Given
            boolean userExists = true;

            when(userRepository.existsByUsername(USERNAME))
                    .thenReturn(userExists);

            //When
            boolean userExistsResult = securityService.userExists(USERNAME);

            //Then
            assertTrue(userExistsResult);
        }
    }
}
