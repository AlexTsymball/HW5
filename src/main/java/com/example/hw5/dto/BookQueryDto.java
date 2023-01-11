package com.example.hw5.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookQueryDto {

    private String name;

    private Integer genreId;

    private int from;

    private int size;

}