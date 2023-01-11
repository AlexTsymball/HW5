package com.example.hw5.controller;


import com.example.hw5.dto.*;
import com.example.hw5.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestResponse createBook(@Valid @RequestBody BookSaveDto dto) {
        int id = bookService.saveBook(dto);
        return new RestResponse(String.valueOf(id));
    }

    @GetMapping("/{id}")
    public BookDetailsDto getBook(@PathVariable int id) {
        return bookService.getBook(id);
    }

    @PostMapping("/_all")
    public List<BookDetailsDto> getAllBook(@RequestBody BookQueryDto query) {
        return bookService.getAllBook(query);
    }

    @PutMapping("/{id}")
    public RestResponse updateBook(@PathVariable int id, @RequestBody BookSaveDto dto) {
        bookService.updateBook(id, dto);
        return new RestResponse("OK");
    }


    @DeleteMapping("delete/{id}")
    public RestResponse deleteBook(@PathVariable int id) {
        bookService.deleteBook(id);
        return new RestResponse("OK");
    }

    @DeleteMapping("deleteAll")
    public RestResponse deleteALlBook() {
        bookService.deleteAllBook();
        return new RestResponse("OK");
    }

    @PostMapping("_search")
    public List<BookInfoDto> search(@RequestBody BookQueryDto query) {
        return bookService.searchByNameAndOrGroup(query);
    }

    @GetMapping("genre")
    public List<String> getGenre() {
        return bookService.getAllGenre();
    }

    @GetMapping("genre/{id}")
    public List<BookInfoDto> getGenreBooks(@PathVariable int id) {
        return bookService.getGenreBooks(id);
    }
}
