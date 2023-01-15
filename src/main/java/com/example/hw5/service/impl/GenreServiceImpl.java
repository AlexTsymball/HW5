package com.example.hw5.service.impl;

import com.example.hw5.data.GenreData;
import com.example.hw5.dto.GenreDetailsDto;
import com.example.hw5.repository.GenreRepository;
import com.example.hw5.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;


    @Override
    public List<GenreDetailsDto> getAllGenre() {
        return genreRepository.getAllGenre().stream()
                .map(this::convertToDetails)
                .toList();
    }

    private GenreDetailsDto convertToDetails(GenreData data) {
        if (data == null) {
            return null;
        }
        return GenreDetailsDto.builder()
                .id(data.getId())
                .name(data.getName())
                .build();
    }

}
