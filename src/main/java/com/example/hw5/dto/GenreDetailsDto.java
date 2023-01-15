package com.example.hw5.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class GenreDetailsDto {

    private final int id;

    private final String name;

}