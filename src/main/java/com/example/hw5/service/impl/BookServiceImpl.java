package com.example.hw5.service.impl;

import com.example.hw5.data.BookData;
import com.example.hw5.data.GenreData;
import com.example.hw5.dto.*;
import com.example.hw5.exception.NotFoundException;
import com.example.hw5.repository.BookRepository;
import com.example.hw5.repository.GenreRepository;
import com.example.hw5.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;


    @Override
    public int saveBook(BookSaveDto dto) {
        BookData data = new BookData(dto.getName(), dto.getAuthor());
        setGenreToBook(data, dto.getGenreId());
        return bookRepository.save(data);
    }

    @Override
    public BookDetailsDto getBook(int id) {
        BookData data = bookRepository.getBookById(id);
        if (data == null) throw new NotFoundException("Book with id %d not found".formatted(id));
        return convertToDetails(data);
    }

    @Override
    @Transactional
    public void updateBook(int id, BookSaveDto dto) {
        BookData book = bookRepository.getBookById(id);
        if (book == null) {
            throw new NotFoundException("Book with id %d not found".formatted(id));
        }
        book.setName(dto.getName() == null ? book.getName() : dto.getName());
        book.setAuthor(dto.getAuthor() == null ? book.getAuthor() : dto.getAuthor());
        setGenreToBook(book, dto.getGenreId());
    }

    @Override
    public List<BookDetailsDto> searchByNameAndOrGroup(BookQueryDto query, Long offset, Long limit) {
        Map<String, Object> params = query.getAllParams();
        paginationAdd(offset, limit, params);
        return bookRepository.searchByNameAndOrGroup(params).stream()
                .map(this::convertToDetails)
                .toList();
    }


    @Override
    public void deleteBook(int id) {
        bookRepository.delete(id);
    }

    @Override
    public List<BookDetailsDto> getAllBook(Long offset, Long limit) {
        Map<String, Object> params = new HashMap<>();
        paginationAdd(offset, limit, params);
        return bookRepository.getAllBook(params).stream()
                .map(this::convertToDetails)
                .toList();
    }


    @Override
    public void deleteAllBook() {
        bookRepository.deleteAll();
    }


    private void paginationAdd(Long offset, Long limit, Map<String, Object> params) {

        if (limit != null) {
            params.put("limit", limit);
            if (offset != null) {
                params.put("offset", offset);
            }
        }
    }


    private void setGenreToBook(BookData data, int genreId) {
        if (genreId != 0) {
            GenreData newGenre = genreRepository.getGenre(genreId);
            if (newGenre == null) {
                throw new NotFoundException("Genre with id %d not found".formatted(genreId));
            }
            data.setGenre(newGenre);
        }
    }

    private BookDetailsDto convertToDetails(BookData data) {
        return BookDetailsDto.builder()
                .id(data.getId())
                .name(data.getName())
                .author(data.getAuthor())
                .genre(convertToDetails(data.getGenre()))
                .build();
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
