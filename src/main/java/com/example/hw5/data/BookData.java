package com.example.hw5.data;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookData {

    private int id;

    private String name;

    private String author;

    private GenreData genre;

}