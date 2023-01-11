package com.example.hw5.dto;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class BookInfoDto {

    private int id;

    private String nameAndAuthor;

    private String genre;

}