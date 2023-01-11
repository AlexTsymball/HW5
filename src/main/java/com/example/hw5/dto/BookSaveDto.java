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
    private String name;

    @NotBlank(message = "author is required")
    private String author;

    @NotNull(message = "genreId is required")
    private Integer genreId;

}