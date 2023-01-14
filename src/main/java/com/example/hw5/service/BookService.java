package com.example.hw5.service;

import com.example.hw5.dto.BookDetailsDto;
import com.example.hw5.dto.BookQueryDto;
import com.example.hw5.dto.BookSaveDto;

import java.util.List;

public interface BookService {
    int saveBook(BookSaveDto dto);

    BookDetailsDto getBook(int id);

    void updateBook(int id, BookSaveDto dto);

    List<BookDetailsDto> searchByNameAndOrGroup(BookQueryDto query);

    void deleteBook(int id);

    List<BookDetailsDto> getAllBook(BookQueryDto query);

    void deleteAllBook();

}
