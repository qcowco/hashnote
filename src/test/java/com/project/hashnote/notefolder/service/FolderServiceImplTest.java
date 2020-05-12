package com.project.hashnote.notefolder.service;

import com.project.hashnote.exceptions.ResourceNotFoundException;
import com.project.hashnote.note.dto.NoteDto;
import com.project.hashnote.notefolder.dao.FolderRepository;
import com.project.hashnote.notefolder.document.Folder;
import com.project.hashnote.notefolder.dto.FolderDto;
import com.project.hashnote.notefolder.dto.FolderRequest;
import com.project.hashnote.notefolder.dto.FolderResponse;
import com.project.hashnote.notefolder.mapper.FolderMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FolderServiceImplTest {
    private final String USERNAME = "USERNAME";
    private final String FOLDER_ID = "ID";
    private final String FOLDER_NAME = "NAME";

    private final String WRONG_ID = "WRONG ID";
    private final String NEW_NAME = "NEW NAME";

    private final String NOTE_ID = "NOTE ID";
    private final String NOTE_NAME = "NOTE NAME";

    @InjectMocks
    private FolderServiceImpl folderService;

    @Mock
    private FolderRepository folderRepository;

    @Mock
    private FolderMapper folderMapper;

    @Captor
    private ArgumentCaptor<Folder> argument;

    @DisplayName("Given a folder is being saved")
    @Nested
    class SaveFolder {

        @DisplayName("Then returns new folder's ID")
        @Test
        public void shouldReturnFoldersId() {
            //Given
            FolderRequest folderRequest = new FolderRequest();
            folderRequest.setName(FOLDER_NAME);

            Folder folder = new Folder();
            folder.setName(FOLDER_NAME);
            folder.setId(FOLDER_ID);

            when(folderMapper.requestToFolder(isA(FolderRequest.class), eq(USERNAME), anyList()))
                    .thenReturn(folder);

            when(folderRepository.save(folder))
                    .thenReturn(folder);

            FolderResponse expectedResponse = new FolderResponse(FOLDER_ID);

            //When
            FolderResponse actualResponse = folderService.save(folderRequest, USERNAME);

            //Then
            assertEquals(expectedResponse, actualResponse);
        }
    }

    @DisplayName("Given user's folders are being fetched")
    @Nested
    class GetFolders {

        @DisplayName("Then returns user's notes")
        @Test
        public void shouldReturnUsersNotes() {
            List<Folder> folders = Arrays.asList(new Folder(), new Folder());

            List<FolderDto> expectedFolderDtos = Arrays.asList(new FolderDto(), new FolderDto());

            when(folderRepository.findByAuthor(USERNAME))
                    .thenReturn(folders);

            when(folderMapper.folderToFolderDtoList(folders))
                    .thenReturn(expectedFolderDtos);

            //When
            List<FolderDto> actualFolderDtos = folderService.getFoldersBy(USERNAME);

            //Then
            assertEquals(expectedFolderDtos, actualFolderDtos);
        }
    }

    @DisplayName("Given a folder is being deleted")
    @Nested
    class DeleteFolder {

        @DisplayName("When note with provided id and author isn't found")
        @Nested
        class NoteNotFound {

            @DisplayName("Then throws ResourceNotFoundException")
            @Test
            public void shouldThrowResourceNotFoundException() {
                //Given
                Folder folder = new Folder();
                folder.setId("some id");

                List<Folder> folders = Arrays.asList(folder);

                when(folderRepository.findByAuthor(USERNAME))
                        .thenReturn(folders);

                //When/Then
                assertThrows(ResourceNotFoundException.class, () -> folderService.delete(FOLDER_ID, USERNAME));
            }
        }
    }

    @DisplayName("Given a folder is being patched")
    @Nested
    class PatchFolder {
        @DisplayName("When folder with provided id and author is found")
        @Nested
        class FolderFound {
            @DisplayName("Then updates folder name to provided value")
            @Test
            public void shouldUpdateFolderName() {
                //Given
                Folder folder = new Folder();
                folder.setId(FOLDER_ID);
                folder.setName(FOLDER_NAME);

                List<Folder> folders = Arrays.asList(folder);

                when(folderRepository.findByAuthor(USERNAME))
                        .thenReturn(folders);

                //When
                folderService.patch(FOLDER_ID, NEW_NAME, USERNAME);

                verify(folderRepository).save(argument.capture());

                //Then
                assertEquals(NEW_NAME, argument.getValue().getName());
            }
        }

        @DisplayName("When folder with provided id and author isn't found")
        @Nested
        class FolderNotFound {

            @DisplayName("Then throws ResourceNotFoundException")
            @Test
            public void shouldThrowResourceNotFoundException() {
                //Given
                Folder folder = new Folder();
                folder.setId(WRONG_ID);

                List<Folder> folders = Arrays.asList(folder);

                when(folderRepository.findByAuthor(USERNAME))
                        .thenReturn(folders);

                //When/Then
                assertThrows(ResourceNotFoundException.class, () -> folderService.patch(FOLDER_ID, NEW_NAME, USERNAME));
            }
        }
    }

    @DisplayName("Given a note is being added to a folder")
    @Nested
    class AddNote {

        @DisplayName("When note already exists in the folder")
        @Nested
        class NoteExists {

            @DisplayName("Then throws IllegalArgumentException")
            @Test
            public void shouldThrowIllegalArgumentException() {
                //Given
                Folder folder = new Folder();
                folder.setId(FOLDER_ID);
                folder.setName(FOLDER_NAME);

                NoteDto noteDto = new NoteDto();
                noteDto.setId(NOTE_ID);

                folder.setNotes(Arrays.asList(noteDto));

                List<Folder> folders = Arrays.asList(folder);

                when(folderRepository.findByAuthor(USERNAME))
                        .thenReturn(folders);

                //When/Then
                assertThrows(IllegalArgumentException.class,
                        () -> folderService.saveToFolder(noteDto, FOLDER_ID, USERNAME)
                );
            }
        }

        @DisplayName("When adding note to folder")
        @Nested
        class AddingNote {

            @DisplayName("Then sets said note's message blank")
            @Test
            public void shouldSetNotesMessageBlank() {
                //Given
                Folder folder = new Folder();
                folder.setId(FOLDER_ID);
                folder.setName(FOLDER_NAME);

                NoteDto noteDto = new NoteDto();
                noteDto.setId(NOTE_ID);

                folder.setNotes(new ArrayList<>());

                List<Folder> folders = Arrays.asList(folder);

                when(folderRepository.findByAuthor(USERNAME))
                        .thenReturn(folders);

                //When
                folderService.saveToFolder(noteDto, FOLDER_ID, USERNAME);

                verify(folderRepository).save(argument.capture());

                NoteDto savedNote = argument.getValue().getNotes().get(0);

                //Then
                assertTrue(StringUtils.isBlank(savedNote.getMessage()));
            }
        }

        @DisplayName("When folder with provided id and author is found")
        @Nested
        class FolderFound {

        }

        @DisplayName("When folder with provided id and author isn't found")
        @Nested
        class FolderNotFound {

            @DisplayName("Then throws ResourceNotFoundException")
            @Test
            public void shouldThrowResourceNotFoundException() {
                //Given
                Folder folder = new Folder();
                folder.setId(WRONG_ID);

                List<Folder> folders = Arrays.asList(folder);

                when(folderRepository.findByAuthor(USERNAME))
                        .thenReturn(folders);

                //When/Then
                assertThrows(ResourceNotFoundException.class, () -> folderService.patch(FOLDER_ID, NEW_NAME, USERNAME));
            }
        }
    }

    @DisplayName("Given a note is being removed from a folder")
    @Nested
    class RemoveNote {

        @DisplayName("When note isn't found in the folder")
        @Nested
        class NoteNotFound {

            @DisplayName("Then throws ResourceNotFoundException")
            @Test
            public void shouldThrowIllegalArgumentException() {
                //Given
                Folder folder = new Folder();
                folder.setId(FOLDER_ID);
                folder.setName(FOLDER_NAME);

                NoteDto noteDto = new NoteDto();
                noteDto.setId(WRONG_ID);

                folder.setNotes(Arrays.asList(noteDto));

                List<Folder> folders = Arrays.asList(folder);

                when(folderRepository.findByAuthor(USERNAME))
                        .thenReturn(folders);

                //When/Then
                assertThrows(ResourceNotFoundException.class,
                        () -> folderService.removeFromFolder(NOTE_ID, FOLDER_ID, USERNAME)
                );
            }
        }

        @DisplayName("When folder with provided id and author isn't found")
        @Nested
        class FolderNotFound {

            @DisplayName("Then throws ResourceNotFoundException")
            @Test
            public void shouldThrowResourceNotFoundException() {
                //Given
                Folder folder = new Folder();
                folder.setId(WRONG_ID);

                List<Folder> folders = Arrays.asList(folder);

                when(folderRepository.findByAuthor(USERNAME))
                        .thenReturn(folders);

                //When/Then
                assertThrows(ResourceNotFoundException.class,
                        () -> folderService.removeFromFolder(NOTE_ID, FOLDER_ID, USERNAME)
                );
            }
        }
    }

    @DisplayName("Given a note is being removed from all folders")
    @Nested
    class RemoveNoteFromAll {

        @DisplayName("Then removes that note from all folders")
        @Test
        public void shouldThrowIllegalArgumentException() {
            //Given
            Folder folder = new Folder();
            folder.setId(FOLDER_ID);
            folder.setName(FOLDER_NAME);

            Folder folderTwo = new Folder();
            folder.setId(FOLDER_ID + 2);
            folder.setName(FOLDER_NAME);

            NoteDto noteDto = new NoteDto();
            noteDto.setId(NOTE_ID);

            List<NoteDto> notes = new ArrayList<>();
            notes.add(noteDto);

            folder.setNotes(notes);

            List<NoteDto> notesTwo = new ArrayList<>();
            notesTwo.add(noteDto);

            folderTwo.setNotes(notesTwo);

            List<Folder> folders = Arrays.asList(folder, folderTwo);

            when(folderRepository.findByNotesContaining(noteDto))
                    .thenReturn(folders);

            //When
            folderService.removeFromAll(noteDto);

            boolean noteRemovedFromAll = folders.stream().allMatch(f -> !f.getNotes().contains(noteDto));

            //Then
            assertTrue(noteRemovedFromAll);
        }
    }
}
// TODO: 12.05.2020 clean up
