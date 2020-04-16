package com.project.hashnote.note.dto;

import java.util.Objects;

public class PatchRequest {
    String name;
    String method;
    private int minutesLeft;
    private int maxVisits;

    public boolean hasMethod() {
        return Objects.nonNull(method);
    }
}
