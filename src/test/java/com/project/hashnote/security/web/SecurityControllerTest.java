package com.project.hashnote.security.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hashnote.security.dto.JwtRequest;
import com.project.hashnote.security.dto.JwtResponse;
import com.project.hashnote.security.service.SecurityService;
import com.project.hashnote.security.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SecurityController.class, useDefaultFilters = false,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ComponentScan(basePackages = "com.project.hashnote.security.web")
@AutoConfigureMockMvc
class SecurityControllerTest {
    private final String LOGIN_URL = "/login";
    private final String REGISTER_URL = "/register";

    private final String USERNAME = "USERNAME";
    private final String PASSWORD = "PASSWORD";

    private final String JWT = "JWT";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SecurityService securityService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private Authentication authentication;

    @Autowired
    private ObjectMapper objectMapper;


    @DisplayName("Given a user is trying to log in")
    @Nested
    class Login {
        private JwtRequest jwtRequest;
        private JwtResponse jwtResponse;

        @BeforeEach
        public void setup() {
            //Given
            jwtRequest = new JwtRequest();
            jwtRequest.setUsername(USERNAME);
            jwtRequest.setPassword(PASSWORD);

            jwtResponse = new JwtResponse(JWT);

            when(authenticationManager.authenticate(any()))
                    .thenReturn(authentication);

            when(jwtTokenUtil.generateToken(USERNAME))
                    .thenReturn(JWT);
        }

        @DisplayName("Then returns a JWT")
        @Test
        public void shouldReturnJwt() throws Exception {
            //When/Then
            mockMvc.perform(
                    post(LOGIN_URL)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(jwtRequest)))
                    .andDo(print())
                    .andExpect(content().json(objectMapper.writeValueAsString(jwtResponse)));
        }

        @DisplayName("Then returns Http status Ok")
        @Test
        public void shouldReturnHttpStatusOk() throws Exception {
            //When
            mockMvc.perform(
                    post(LOGIN_URL)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(jwtRequest)))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @DisplayName("Given an account is being registered")
    @Nested
    class Register {

        @DisplayName("Then returns Http status Created")
        @Test
        public void shouldReturnHttpStatusCreated() throws Exception {
            //Given
            JwtRequest jwtRequest = new JwtRequest();
            jwtRequest.setUsername(USERNAME);
            jwtRequest.setPassword(PASSWORD);

            when(authenticationManager.authenticate(any()))
                    .thenReturn(authentication);

            when(jwtTokenUtil.generateToken(USERNAME))
                    .thenReturn(JWT);

            //When/Then
            mockMvc.perform(
                    post(REGISTER_URL)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(jwtRequest)))
                    .andDo(print())
                    .andExpect(status().isCreated());
        }
    }

}
