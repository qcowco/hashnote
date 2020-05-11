package com.project.hashnote.note.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hashnote.note.dto.EncryptionResponse;
import com.project.hashnote.note.dto.NoteDto;
import com.project.hashnote.note.dto.NoteRequest;
import com.project.hashnote.note.service.NoteService;
import com.project.hashnote.notefolder.service.FolderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NoteController.class, useDefaultFilters = false,
        excludeAutoConfiguration = HypermediaAutoConfiguration.class)
@ComponentScan(basePackages = "com.project.hashnote.note.web")
@AutoConfigureMockMvc
class NoteControllerTest {
    private final String USERNAME = "MOCK_USER";

    private final String NOTE_ID = "ID";
    private final String NOTE_NAME = "NAME";
    private final String NOTE_MESSAGE = "MESSAGE";
    private final String ENCRYPTION_METHOD = "METHOD";

    private final String NOTE_KEY = "KEY";

    private final String BASE_URL = "/api/v1/notes";
    private final String DECRYPTED_NOTE_URL = BASE_URL + '/' + NOTE_ID + "/keys/" + NOTE_KEY;
    private final String NOTE_URL = BASE_URL + '/' + NOTE_ID;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NoteService noteService;

    @MockBean
    private FolderService folderService;

    @DisplayName("Given users notes are being fetched")
    @Nested
    @WithMockUser(username = USERNAME)
    class GetAll {
        private List<NoteDto> noteDtos;

        @BeforeEach
        public void setup() {
            //Given
            noteDtos = Arrays.asList(new NoteDto(), new NoteDto(), new NoteDto());

            when(noteService.getAllBy(USERNAME))
                    .thenReturn(noteDtos);
        }

        @DisplayName("Then returns HTTP status OK")
        @Test
        public void shouldReturnHttpStatusOk() throws Exception {
            //When/Then
            mockMvc.perform(get(BASE_URL))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @DisplayName("Then returns his notes")
        @Test
        public void shouldReturnAuthenticatedUsersNotes() throws Exception {
            //When/Then
            mockMvc.perform(get(BASE_URL))
                    .andDo(print())
                    .andExpect(content().json(objectMapper.writeValueAsString(noteDtos)));
        }
    }

    @DisplayName("Given a note is being fetched")
    @Nested
    @WithMockUser(username = USERNAME)
    class GetOne {
        private NoteDto noteDto;

        @BeforeEach
        public void setup() {
            //Given
            noteDto = new NoteDto();

            noteDto.setId(NOTE_ID);
            noteDto.setName(NOTE_NAME);
            noteDto.setMessage(NOTE_MESSAGE);

            when(noteService.getOne(NOTE_ID))
                    .thenReturn(noteDto);
        }

        @DisplayName("Then returns HTTP status OK")
        @Test
        public void shouldReturnHttpStatusOk() throws Exception {
            //When/Then
            mockMvc.perform(get(NOTE_URL))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @DisplayName("Then returns note")
        @Test
        public void shouldReturnNote() throws Exception {
            //When/Then
            mockMvc.perform(get(NOTE_URL))
                    .andDo(print())
                    .andExpect(content().json(objectMapper.writeValueAsString(noteDto)));
        }
    }

    @DisplayName("Given a note is being decrypted")
    @Nested
    @WithMockUser(username = USERNAME)
    class GetDecrypoted {
        private NoteDto noteDto;

        @BeforeEach
        public void setup() {
            //Given
            noteDto = new NoteDto();

            noteDto.setId(NOTE_ID);
            noteDto.setName(NOTE_NAME);
            noteDto.setMessage(NOTE_MESSAGE);

            when(noteService.getDecrypted(NOTE_ID, NOTE_KEY))
                    .thenReturn(noteDto);
        }

        @DisplayName("Then returns HTTP status Ok")
        @Test
        public void shouldReturnHttpStatusOk() throws Exception {
            //When/Then
            mockMvc.perform(get(DECRYPTED_NOTE_URL))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @DisplayName("Then returns decrypted note")
        @Test
        public void shouldReturnDecryptedNote() throws Exception {
            //When/Then
            mockMvc.perform(get(DECRYPTED_NOTE_URL))
                    .andDo(print())
                    .andExpect(content().json(objectMapper.writeValueAsString(noteDto)));
        }

    }

    @DisplayName("Given a note is being created")
    @Nested
    class SaveNote {

        @DisplayName("When note request fails validation check")
        @Nested
        @WithMockUser(username = USERNAME)
        class RequestInvalid {

            @DisplayName("Then returns HTTP status Bad Request")
            @Test
            public void shouldReturnHttpStatusBadRequest() throws Exception {
                //Given
                NoteRequest noteRequest = new NoteRequest();

                //When/Then
                mockMvc.perform(
                        post(BASE_URL)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(noteRequest)))
                        .andDo(print())
                        .andExpect(status().isBadRequest());
            }
        }

        @DisplayName("When note request is valid")
        @Nested
        @WithMockUser(username = USERNAME)
        class NoteCreated {
            private NoteRequest noteRequest;
            private EncryptionResponse encryptionResponse;

            @BeforeEach
            public void setup() throws JsonProcessingException {
                //Given
                noteRequest = new NoteRequest();
                noteRequest.setName(NOTE_NAME);
                noteRequest.setMessage(NOTE_MESSAGE);
                noteRequest.setMethod(ENCRYPTION_METHOD);

                encryptionResponse = new EncryptionResponse(NOTE_ID, NOTE_KEY);

                when(noteService.save(any(), eq(USERNAME)))
                        .thenReturn(encryptionResponse);
            }

            @DisplayName("Then returns HTTP status Created")
            @Test
            public void shouldReturnHttpStatusCreated() throws Exception {
                //When/Then
                mockMvc.perform(
                        post(BASE_URL)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(noteRequest)))
                        .andDo(print())
                        .andExpect(status().isCreated());
            }

            @DisplayName("Then returns note credentials")
            @Test
            public void shouldReturnNoteCredentials() throws Exception {
                //When/Then
                mockMvc.perform(
                        post(BASE_URL)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(noteRequest)))
                        .andDo(print())
                        .andExpect(content().json(objectMapper.writeValueAsString(encryptionResponse)));
            }
        }

//        @DisplayName("When user isn't logged in")
//        @Nested
//        class UserLogged {
//            NoteRequest noteRequest;
//            EncryptionResponse encryptionResponse;
//
//            @DisplayName("Then sets note author to placeholder")
//            @Test
//            public void shouldSetNoteAuthorToPlaceholder() throws Exception {
//                //Given
//                noteRequest = new NoteRequest();
//                noteRequest.setName(NOTE_NAME);
//                noteRequest.setMessage(NOTE_MESSAGE);
//                noteRequest.setMethod(ENCRYPTION_METHOD);
//
//                encryptionResponse = new EncryptionResponse(NOTE_ID, NOTE_KEY);
//
//                when(noteService.save(any(), Mockito.matches("/.*/")))
//                        .thenReturn(encryptionResponse);
//
//                //When/Then
//                assertDoesNotThrow(() ->
//                        mockMvc.perform(
//                                post(BASE_URL)
//                                        .with(csrf())
//                                        .contentType(MediaType.APPLICATION_JSON)
//                                        .content(objectMapper.writeValueAsBytes(noteRequest)))
//                                .andDo(print())
//                );
//            }
//        }
    }

    @DisplayName("Given an unencrypted note is being patched")
    @Nested
    class PatchUnencrypted {

        @DisplayName("When patching with encryption")
        @Nested
        @WithMockUser(username = USERNAME)
        class PatchWithEncryption {
            private NoteRequest noteRequest;
            private EncryptionResponse encryptionResponse;

            @BeforeEach
            public void setup() {
                //Given
                noteRequest = new NoteRequest();
                noteRequest.setName(NOTE_NAME);
                noteRequest.setMessage(NOTE_MESSAGE);
                noteRequest.setMethod(ENCRYPTION_METHOD);

                encryptionResponse = new EncryptionResponse(NOTE_ID, NOTE_KEY);

                when(noteService.patch(any(), eq(USERNAME), eq(NOTE_ID)))
                        .thenReturn(encryptionResponse);
            }

            @DisplayName("Then returns HTTP status Ok")
            @Test
            public void shouldReturnHttpStatusOk() throws Exception {
                //When/Then
                mockMvc.perform(
                        patch(NOTE_URL)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(noteRequest)))
                        .andDo(print())
                        .andExpect(status().isOk());
            }

            @DisplayName("Then returns encryption credentials with: ID, KEY")
            @Test
            public void shouldReturnEncryptionCredentials() throws Exception {
                //When/Then
                mockMvc.perform(
                        patch(NOTE_URL)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(noteRequest)))
                        .andDo(print())
                        .andExpect(content().json(objectMapper.writeValueAsString(encryptionResponse)));
            }
        }

        @DisplayName("When patching without encryption")
        @Nested
        @WithMockUser(username = USERNAME)
        class PatchWithoutEncryption {
            private NoteRequest noteRequest;
            private EncryptionResponse encryptionResponse;

            @BeforeEach
            public void setup() {
                //Given
                noteRequest = new NoteRequest();
                noteRequest.setName(NOTE_NAME);
                noteRequest.setMessage(NOTE_MESSAGE);

                encryptionResponse = new EncryptionResponse(NOTE_ID);

                when(noteService.patch(any(), eq(USERNAME), eq(NOTE_ID)))
                        .thenReturn(encryptionResponse);
            }

            @DisplayName("Then returns HTTP status Ok")
            @Test
            public void shouldReturnHttpStatusOk() throws Exception {
                //When/Then
                mockMvc.perform(
                        patch(NOTE_URL)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(noteRequest)))
                        .andDo(print())
                        .andExpect(status().isOk());
            }

            @DisplayName("Then returns encryption credentials with: ID")
            @Test
            public void shouldReturnEncryptionCredentials() throws Exception {
                //When/Then
                mockMvc.perform(
                        patch(NOTE_URL)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(noteRequest)))
                        .andDo(print())
                        .andExpect(content().json(objectMapper.writeValueAsString(encryptionResponse)));
            }
        }

    }

    @DisplayName("Given an encrypted note is being patched")
    @Nested
    class PatchEncrypted {

        @DisplayName("When patching with encryption")
        @Nested
        @WithMockUser(username = USERNAME)
        class PatchWithEncryption {
            private NoteRequest noteRequest;
            private EncryptionResponse encryptionResponse;

            @BeforeEach
            public void setup() {
                //Given
                noteRequest = new NoteRequest();
                noteRequest.setName(NOTE_NAME);
                noteRequest.setMessage(NOTE_MESSAGE);
                noteRequest.setMethod(ENCRYPTION_METHOD);

                encryptionResponse = new EncryptionResponse(NOTE_ID, NOTE_KEY);

                when(noteService.patch(any(), eq(USERNAME), eq(NOTE_ID), eq(NOTE_KEY)))
                        .thenReturn(encryptionResponse);
            }

            @DisplayName("Then returns HTTP status Ok")
            @Test
            public void shouldReturnHttpStatusOk() throws Exception {
                //When/Then
                mockMvc.perform(
                        patch(DECRYPTED_NOTE_URL)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(noteRequest)))
                        .andDo(print())
                        .andExpect(status().isOk());
            }

            @DisplayName("Then returns encryption credentials with: ID, KEY")
            @Test
            public void shouldReturnEncryptionCredentials() throws Exception {
                //When/Then
                mockMvc.perform(
                        patch(DECRYPTED_NOTE_URL)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(noteRequest)))
                        .andDo(print())
                        .andExpect(content().json(objectMapper.writeValueAsString(encryptionResponse)));
            }
        }

        @DisplayName("When patching without encryption")
        @Nested
        @WithMockUser(username = USERNAME)
        class PatchWithoutEncryption {
            private NoteRequest noteRequest;
            private EncryptionResponse encryptionResponse;

            @BeforeEach
            public void setup() {
                //Given
                noteRequest = new NoteRequest();
                noteRequest.setName(NOTE_NAME);
                noteRequest.setMessage(NOTE_MESSAGE);

                encryptionResponse = new EncryptionResponse(NOTE_ID);

                when(noteService.patch(any(), eq(USERNAME), eq(NOTE_ID), eq(NOTE_KEY)))
                        .thenReturn(encryptionResponse);
            }

            @DisplayName("Then returns HTTP status Ok")
            @Test
            public void shouldReturnHttpStatusOk() throws Exception {
                //When/Then
                mockMvc.perform(
                        patch(DECRYPTED_NOTE_URL)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(noteRequest)))
                        .andDo(print())
                        .andExpect(status().isOk());
            }

            @DisplayName("Then returns encryption credentials with: ID")
            @Test
            public void shouldReturnEncryptionCredentials() throws Exception {
                //When/Then
                mockMvc.perform(
                        patch(DECRYPTED_NOTE_URL)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(noteRequest)))
                        .andDo(print())
                        .andExpect(content().json(objectMapper.writeValueAsString(encryptionResponse)));
            }
        }

    }

    @DisplayName("Given a note is being deleted")
    @Nested
    @WithMockUser(username = USERNAME)
    class DeleteNote {

        @DisplayName("Then returns Http status No Content")
        @Test
        public void shouldReturnHttpNoContent() throws Exception {
            //Given
            NoteDto noteDto = new NoteDto();
            noteDto.setId(NOTE_ID);

            when(noteService.getOne(NOTE_ID))
                    .thenReturn(noteDto);

            //When/Then
            mockMvc.perform(
                    delete(NOTE_URL)
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }
    }
}
