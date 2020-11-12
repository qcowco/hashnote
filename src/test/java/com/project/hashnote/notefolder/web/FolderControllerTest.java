package com.project.hashnote.notefolder.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hashnote.note.dto.NoteDto;
import com.project.hashnote.note.service.NoteService;
import com.project.hashnote.notefolder.dto.FolderDto;
import com.project.hashnote.notefolder.dto.FolderRequest;
import com.project.hashnote.notefolder.dto.FolderResponse;
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
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.ArrayList;
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

@WebMvcTest(controllers = FolderController.class, useDefaultFilters = false,
        excludeAutoConfiguration = HypermediaAutoConfiguration.class)
@ComponentScan(basePackages = "com.project.hashnote.notefolder.web")
@AutoConfigureMockMvc
class FolderControllerTest {
    private final String USERNAME = "MOCK_USER";

    private final String FOLDER_ID = "FOLDER_ID";
    private final String FOLDER_NAME = "FOLDER_NAME";

    private final String NOTE_ID = "NOTE_ID";

    private final String BASE_URL = "/api/v1/folders";
    private final String FOLDER_URL = BASE_URL + '/' + FOLDER_ID;
    private final String FOLDER_NOTE_URL = FOLDER_URL + "/notes/" + NOTE_ID;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FolderService folderService;

    @MockBean
    private NoteService noteService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("Given folders are being fetched")
    @Nested
    @WithMockUser(username = USERNAME)
    class GetUserFolders {
        private FolderDto folderDto;
        private List<FolderDto> folderDtos;

        @BeforeEach
        public void setup() {
            //Given
            folderDto = new FolderDto();
            folderDto.setId(FOLDER_ID);
            folderDto.setName(FOLDER_NAME);
            folderDto.setAuthor(USERNAME);
            folderDto.setNotes(new ArrayList<>());

            folderDtos = Arrays.asList(folderDto, folderDto, folderDto);

            when(folderService.getFoldersBy(USERNAME))
                    .thenReturn(folderDtos);
        }

        @DisplayName("Then return Http status Ok")
        @Test
        public void shouldReturnHttpStatusOk() throws Exception {
            //When/Then
            mockMvc.perform(get(BASE_URL))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @DisplayName("Then return users folders")
        @Test
        public void shouldReturnUsersFolders() throws Exception {
            //When/Then
            mockMvc.perform(get(BASE_URL))
                    .andDo(print())
                    .andExpect(content().json(objectMapper.writeValueAsString(folderDtos)));
        }
    }

    @DisplayName("Given a folder is being created")
    @Nested
    class CreateFolder {
        private FolderRequest folderRequest;
        private FolderResponse folderResponse;

        @BeforeEach
        public void setup() {
            //Given
            folderRequest = new FolderRequest();
            folderRequest.setName(FOLDER_NAME);

            folderResponse = new FolderResponse(FOLDER_ID);

            when(folderService.save(any(), eq(USERNAME)))
                    .thenReturn(folderResponse);
        }

        @DisplayName("When request is valid")
        @Nested
        @WithMockUser(username = USERNAME)
        class ValidRequest {

            @DisplayName("Then returns Http status Created")
            @Test
            public void shouldReturnHttpStatusCreated() throws Exception {
                //When/Then
                mockMvc.perform(
                        post(BASE_URL)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(folderRequest)))
                        .andDo(print())
                        .andExpect(status().isCreated());
            }

            @DisplayName("Then returns new folder ID")
            @Test
            public void shouldReturnNewFolderId() throws Exception {
                //When/Then
                mockMvc.perform(
                        post(BASE_URL)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(folderRequest)))
                        .andDo(print())
                        .andExpect(content().json(objectMapper.writeValueAsString(folderResponse)));
            }
        }

        @DisplayName("When request fails validation check")
        @Nested
        @WithMockUser(username = USERNAME)
        class InvalidRequest {
            private FolderRequest emptyRequest = new FolderRequest();

            @DisplayName("Then throws Http status Bad Request")
            @Test
            public void shouldReturnHttpStatusBadRequest() throws Exception {
                //When/Then
                mockMvc.perform(
                        post(BASE_URL)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(emptyRequest)))
                        .andDo(print())
                        .andExpect(status().isBadRequest());
            }
        }
    }

    @DisplayName("Given a folder is being deleted")
    @Nested
    @WithMockUser(username = USERNAME)
    class DeleteFolder {

        @DisplayName("Then returns Http status Ok")
        @Test
        public void shouldReturnHttpStatusOk() throws Exception {
            //When/Then
            mockMvc.perform(delete(FOLDER_URL)
                    .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @DisplayName("Then returns empty body")
        @Test
        public void shouldReturnEmptyBody() throws Exception {
            //When/Then
            mockMvc.perform(delete(FOLDER_URL)
                    .with(csrf()))
                    .andDo(print())
                    .andExpect(isEmpty());
        }
    }

    @DisplayName("Given a folder is being patched")
    @Nested
    class PatchFolder {
        private FolderRequest folderRequest;

        @BeforeEach
        public void setup() {
            folderRequest = new FolderRequest();
            folderRequest.setName(FOLDER_NAME);
        }

        @DisplayName("When request is valid")
        @Nested
        @WithMockUser(username = USERNAME)
        class ValidRequest {

            @DisplayName("Then returns Http status Ok")
            @Test
            public void shouldReturnHttpStatusOk() throws Exception {
                //When/Then
                mockMvc.perform(
                        patch(FOLDER_URL)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(folderRequest)))
                        .andDo(print())
                        .andExpect(status().isOk());
            }

            @DisplayName("Then returns empty body")
            @Test
            public void shouldReturnEmptyBody() throws Exception {
                //When/Then
                mockMvc.perform(
                        patch(FOLDER_URL)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(folderRequest)))
                        .andDo(print())
                        .andExpect(isEmpty());
            }
        }

        @DisplayName("When request fails validation check")
        @Nested
        @WithMockUser(username = USERNAME)
        class InvalidRequest {

            @DisplayName("Then returns Http status Bad Request")
            @Test
            public void shouldReturnHttpStatusOk() throws Exception {
                //When/Then
                mockMvc.perform(
                        patch(FOLDER_URL)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new FolderRequest())))
                        .andDo(print())
                        .andExpect(status().isBadRequest());
            }
        }


    }

    @DisplayName("Given a note is being added to a folder")
    @Nested
    @WithMockUser(username = USERNAME)
    class AddNote {
        private NoteDto noteDto;

        @BeforeEach
        public void setup() {
            //Given
            noteDto = new NoteDto();

            when(noteService.getOne(NOTE_ID))
                    .thenReturn(noteDto);
        }

        @DisplayName("Then returns Http status Ok")
        @Test
        public void shouldReturnHttpStatusOk() throws Exception {
            //When/Then
            mockMvc.perform(
                    post(FOLDER_NOTE_URL)
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @DisplayName("Then returns empty body")
        @Test
        public void shouldReturnEmptyBody() throws Exception {
            //When/Then
            mockMvc.perform(
                    post(FOLDER_NOTE_URL)
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(isEmpty());
        }
    }

    @DisplayName("Given a note is being deleted from a folder")
    @Nested
    @WithMockUser(username = USERNAME)
    class RemoveNote {

        @DisplayName("Then returns Http status Ok")
        @Test
        public void shouldReturnHttpStatusOk() throws Exception {
            //When/Then
            mockMvc.perform(
                    delete(FOLDER_NOTE_URL)
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @DisplayName("Then returns empty body")
        @Test
        public void shouldReturnEmptyBody() throws Exception {
            //When/Then
            mockMvc.perform(
                    delete(FOLDER_NOTE_URL)
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(isEmpty());
        }
    }

    private ResultMatcher isEmpty() {
        return content().bytes(new byte[0]);
    }
}
