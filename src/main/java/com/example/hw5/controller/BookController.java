package com.example.hw5.controller;


import com.example.hw5.dto.*;
import com.example.hw5.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Validated
public class BookController {

    private final BookService bookService;

    @PostMapping("/create")
    public ResponseEntity<String> createBook(@Valid @RequestBody BookSaveDto dto) {
        int id = bookService.saveBook(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("The Book with id " + id + " is saved");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDetailsDto> getBook(@PathVariable int id) {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getBook(id));
    }

    @GetMapping
    public ResponseEntity<List<BookDetailsDto>> getAllBooks(
            @RequestParam(required = false) @Min(value = 0, message = "min offset value 0") Long offset,
            @RequestParam(required = false) @Min(value = 1, message = "min limit value 1") Long limit) {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getAllBook(offset, limit));
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
    public ResponseEntity<List<BookDetailsDto>> search(@RequestBody BookQueryDto query,
                                                       @Min(value = 0, message = "min offset value 0")
                                                       @RequestParam(required = false) Long offset,
                                                       @Min(value = 1, message = "min limit value 1")
                                                       @RequestParam(required = false) Long limit) {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.searchByNameAndOrGroup(query, offset, limit));
    }

}
