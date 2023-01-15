package com.example.hw5.data;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "book")
@Table(name = "book")
public class BookData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String author;

    @ManyToOne
    @JoinColumn(name = "id_genre")
    private GenreData genre;

    public BookData(String name, String author) {
        this.name = name;
        this.author = author;
    }
}