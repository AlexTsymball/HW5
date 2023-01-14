package com.example.hw5.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@Jacksonized
public class BookSaveDto {

    @NotBlank(message = "name is required")
    private final String name;

    @NotBlank(message = "author is required")
    private final String author;

    @NotNull(message = "genreId is required")
    private final int genreId;
}