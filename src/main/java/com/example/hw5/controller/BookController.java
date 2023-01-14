package com.example.hw5.controller;


import com.example.hw5.dto.*;
import com.example.hw5.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping("/create")
    public ResponseEntity<String> createBook(@Valid @RequestBody BookSaveDto dto) {
        int id = bookService.saveBook(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("The Book with id " + id + " is saved");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDetailsDto> getBook(@PathVariable int id) {
        return  ResponseEntity.status(HttpStatus.OK).body(bookService.getBook(id));
    }

    @PostMapping
    public ResponseEntity<List<BookDetailsDto>> getAllBooks(@RequestBody BookQueryDto query) {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getAllBook(query));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateBook(@PathVariable int id, @RequestBody BookSaveDto dto) {
        bookService.updateBook(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body("The book with id " + id + " is updated.");
    }


    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable int id) {
        bookService.deleteBook(id);
        return ResponseEntity.status(HttpStatus.OK).body("The book with id " + id + " is deleted.");
    }

    @DeleteMapping("deleteAll")
    public ResponseEntity<String> deleteAllBooks() {
        bookService.deleteAllBook();
        return ResponseEntity.status(HttpStatus.OK).body("All books are deleted.");
    }

    @PostMapping("_search")
    public ResponseEntity<List<BookDetailsDto>> search(@RequestBody BookQueryDto query) {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.searchByNameAndOrGroup(query));
    }

}
