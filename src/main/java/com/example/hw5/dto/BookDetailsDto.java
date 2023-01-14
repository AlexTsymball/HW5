package com.example.hw5.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookDetailsDto {

    private final int id;

    private final String name;

    private final String author;

    private final GenreDetailsDto genre;
}