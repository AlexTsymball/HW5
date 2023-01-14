package com.example.hw5.controller;


import com.example.hw5.dto.GenreDetailsDto;
import com.example.hw5.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;



    @GetMapping()
    public ResponseEntity<List<GenreDetailsDto>> getGenres(){
        return ResponseEntity.status(HttpStatus.OK).body(genreService.getAllGenre());
    }

}

