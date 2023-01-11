package com.example.hw5.dto;

import com.example.hw5.data.GenreData;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookDetailsDto {

    private int id;

    private String name;

    private String author;

    private GenreData genre;
}