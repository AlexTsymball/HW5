package com.example.hw5.service;

import com.example.hw5.data.BookData;
import com.example.hw5.data.GenreData;
import com.example.hw5.dto.*;
import com.example.hw5.repository.BookRepository;
import com.example.hw5.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final GenreRepository genreRepository;

    @Override
    public int saveBook(BookSaveDto dto) {
        BookData data = new BookData();
        updateDataFromDto(data, dto);
        return bookRepository.save(data);
    }

    @Override
//    @Transactional(readOnly = true)
    public BookDetailsDto getBook(int id) {
        BookData data = bookRepository.getBookById(id);
        return convertToDetails(data);
    }

    @Override
    public void updateBook(int id, BookSaveDto dto) {
        BookData data = new BookData();
        data.setId(id);
        updateDataFromDto(data, dto);
        bookRepository.updateBook(data);
    }

    @Override
//    @Transactional(readOnly = true)
    public List<BookInfoDto> searchByNameAndOrGroup(BookQueryDto query) {
        return bookRepository.searchByNameAndOrGroup(query).stream()
                .map(this::toInfoDto)
                .toList();
    }

    @Override
//    @Transactional(readOnly = true)
    public List<String> getAllGenre() {
        return genreRepository.getAllGenre();
    }

    @Override
//    @Transactional(readOnly = true)
    public List<BookInfoDto> getGenreBooks(int id) {
        BookQueryDto query =  new BookQueryDto();
        query.setGenreId(id);
        return bookRepository.searchByNameAndOrGroup(query).stream()
                .map(this::toInfoDto)
                .toList();
    }

    @Override
    public void deleteBook(int id) {
        bookRepository.delete(id);
    }

    @Override
    public List<BookDetailsDto> getAllBook(BookQueryDto query) {
        return bookRepository.getAllBook(query).stream()
                .map(this::convertToDetails)
                .toList();
    }

    @Override
    public void deleteAllBook() {
        bookRepository.deleteAll();
    }

    private BookInfoDto toInfoDto(BookData data) {
        return BookInfoDto.builder()
                .id(data.getId())
                .nameAndAuthor(data.getName() + " " + data.getAuthor())
                .genre(data.getGenre() != null ? data.getGenre().getName() : null)
                .build();
    }

    private void updateDataFromDto(BookData data, BookSaveDto dto) {
        data.setName(dto.getName() != null ? dto.getName() : data.getName());
        data.setAuthor(dto.getAuthor() != null ? dto.getAuthor() : data.getAuthor());
        data.setGenre(resolveGenre(dto.getGenreId()) != null ? resolveGenre(dto.getGenreId()) : data.getGenre());
    }

    private GenreData resolveGenre(Integer genreId) {
        if (genreId == null) {
            return null;
        }
        return genreRepository.get(genreId);
    }

    private BookDetailsDto convertToDetails(BookData data) {
        return BookDetailsDto.builder()
                .id(data.getId())
                .name(data.getName())
                .author(data.getAuthor())
                .genre(data.getGenre())
                .build();
    }

}
