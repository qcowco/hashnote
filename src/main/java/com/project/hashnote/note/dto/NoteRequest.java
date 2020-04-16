package com.project.hashnote.note.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoteRequest {
    @NotNull
    private String name;
    @NotNull
    private String message;
    private String method;
    @Min(0)
    private int minutesLeft;
    @Min(0)
    private int maxVisits;

    public boolean hasMethod() {
        return StringUtils.hasText(method);
    }
}
