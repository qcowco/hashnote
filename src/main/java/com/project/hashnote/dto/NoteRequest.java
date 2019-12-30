package com.project.hashnote.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoteRequest {
    private NoteDto noteDto;
    private EncodingDetails encodingDetails;
}
