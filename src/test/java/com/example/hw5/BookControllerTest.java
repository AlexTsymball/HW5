package com.example.hw5;

import com.example.hw5.data.BookData;
import com.example.hw5.data.GenreData;
import com.example.hw5.dto.BookDetailsDto;
import com.example.hw5.dto.BookQueryDto;
import com.example.hw5.repository.BookRepository;
import com.example.hw5.service.BookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Hw5Application.class)
@AutoConfigureMockMvc
public class BookControllerTest {

    private static String name = "book1";
    private static String author = "Author";
    private static GenreData genreData = new GenreData(2,"historical");

    @Autowired
    private MockMvc mvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void cleaningTable() {
        bookService.deleteAllBook();
    }

    private <T>T parseResponse(MvcResult mvcResult, Class<T> c) {
        try {
            return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), c);
        } catch (JsonProcessingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Error parsing json", e);
        }
    }


    @Test
    public void testCreateBook() throws Exception {
        int genreId = 2;
        String body = """
          {
              "name": "%s",
              "author": "%s",
              "genreId": %d
          }
        """.formatted(name, author, genreId);
        mvc.perform(post("/api/books/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
                .andExpect(status().isCreated())
                .andReturn();

        BookData book = bookRepository.getBookById(1);
        assertThat(book).isNotNull();
        assertThat(book.getName()).isEqualTo(name);
        assertThat(book.getAuthor()).isEqualTo(author);
        assertThat(book.getGenre().getId()).isEqualTo(genreId);
    }

    @Test
    public void testCreateBook_validation() throws Exception {
        mvc.perform(post("/api/books/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetBook() throws Exception {
        BookData bookData = BookData.builder()
                .name(name)
                .author(author)
                .genre(genreData)
                .build();

        int bookId = bookRepository.save(bookData);
        MvcResult mvcResult = mvc.perform(get("/api/books/{id}" , bookId))
                .andReturn();

        BookDetailsDto response = parseResponse(mvcResult, BookDetailsDto.class);
        assertThat(response).isNotNull();
        assertEquals(response.getId(), 1);
        assertThat(response.getAuthor()).isEqualTo(author);
        assertThat(response.getGenre().getName()).isEqualTo(genreData.getName());
        assertThat(response.getGenre().getId()).isEqualTo(genreData.getId());
    }

    @Test
    public void testGetBook_notFound() throws Exception {

        BookData bookData = BookData.builder()
                .name(name)
                .author(author)
                .genre(genreData)
                .build();


        int bookId = bookRepository.save(bookData);
        mvc.perform(get("/api/books/{id}" , bookId+1))
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertEquals("Book with id %d not found".formatted(bookId+1), result.getResolvedException().getMessage()));
    }

    @Test
    public void testUpdateBook() throws Exception {
        String nameNew = "bookAfterChange";
        String body = """
          {
              "name": "%s",
              "author": "%s",
              "genreId": %d
          }
        """.formatted(nameNew, author, genreData.getId());

        BookData bookData = BookData.builder()
                .name(name)
                .author(author)
                .genre(genreData)
                .build();

        int bookId = bookRepository.save(bookData);

        MvcResult mvcResult = mvc.perform(put("/api/books/{id}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        assertEquals(result, "The book with id " + bookId + " is updated.");

        BookData book = bookRepository.getBookById(bookId);
        assertThat(book).isNotNull();
        assertThat(book.getName()).isEqualTo(nameNew);
        assertThat(book.getId()).isEqualTo(bookId);
        assertThat(book.getAuthor()).isEqualTo(author);
        assertThat(book.getGenre().getId()).isEqualTo(genreData.getId());
    }

    @Test
    public void testUpdateBook_notFound() throws Exception {

        String nameNew = "bookAfterChange";
        String body = """
          {
              "name": "%s",
              "author": "%s",
              "genreId": %d
          }
        """.formatted(nameNew, author, genreData.getId());

        BookData bookData = BookData.builder()
                .name(name)
                .author(author)
                .genre(genreData)
                .build();

        int bookId = bookRepository.save(bookData);

      mvc.perform(put("/api/books/{id}", bookId+1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertEquals("Book with id %d not found".formatted(bookId+1), result.getResolvedException().getMessage()));
    }

    @Test
    public void testUpdateBook_withNotAllValues() throws Exception {
        String nameNew = "bookNew";
        String body = """
          {
              "name": "%s"
          }
        """.formatted(nameNew);
        BookData bookData = BookData.builder()
                .name(name)
                .author(author)
                .genre(genreData)
                .build();

        int bookId = bookRepository.save(bookData);
        MvcResult mvcResult = mvc.perform(put("/api/books/{id}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
              .andExpect(status().isOk())
              .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        assertThat(result).isEqualTo("The book with id " + bookId + " is updated.");

        BookData book = bookRepository.getBookById(bookId);
        assertThat(book).isNotNull();
        assertThat(book.getName()).isEqualTo(nameNew);
        assertThat(book.getId()).isEqualTo(bookId);
        assertThat(book.getAuthor()).isEqualTo(author);
        assertThat(book.getGenre().getId()).isEqualTo(genreData.getId());
    }

    @Test
    public void testGetAllBook() throws Exception {
        BookData bookData = BookData.builder()
                .name(name)
                .author(author)
                .genre(genreData)
                .build();

        bookRepository.save(bookData);

        BookData bookData2 = BookData.builder()
                .name(name + "2")
                .author(author)
                .genre(genreData)
                .build();


        bookRepository.save(bookData2);

        mvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",
                        is(2)));

    }

    @Test
    public void testGetAllBook_paginator() throws Exception {
        BookData bookData = BookData.builder()
                .name(name)
                .author(author)
                .genre(genreData)
                .build();

        bookRepository.save(bookData);

        BookData bookData2 = BookData.builder()
                .name(name + "2")
                .author(author)
                .genre(genreData)
                .build();


        bookRepository.save(bookData2);

        mvc.perform(get("/api/books?offset=1&limit=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",
                        is(1)));

    }

    @Test
    public void testSearch_bookName() throws Exception {
        String body = """
          {
              "name": "%s"
          }
        """.formatted(name);
        BookData bookData = BookData.builder()
                .name(name)
                .author(author)
                .genre(genreData)
                .build();

        bookRepository.save(bookData);

        BookData bookData2 = BookData.builder()
                .name(name + "2")
                .author(author)
                .genre(genreData)
                .build();


        bookRepository.save(bookData2);

        mvc.perform(post("/api/books/_search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
              .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",
                        is(1)))
                .andExpect(jsonPath("$[0].name",
                        is(name)))
                .andExpect(jsonPath("$[0].author",
                        is(author)));

    }

    @Test
    public void testSearch_idGenre() throws Exception {
        String body = """
          {
              "genreId": "%d"
          }
        """.formatted(genreData.getId());

        BookData bookData = BookData.builder()
                .name(name)
                .author(author)
                .genre(genreData)
                .build();

        bookRepository.save(bookData);

        BookData bookData2 = BookData.builder()
                .name(name+"2")
                .author(author)
                .genre(genreData)
                .build();


        bookRepository.save(bookData2);

        mvc.perform(post("/api/books/_search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
              .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",
                        is(2)))
                .andExpect(jsonPath("$[0].genre.name",
                        is(genreData.getName())));

    }


    @Test
    public void testSearch_emptyBody() throws Exception {

        BookData bookData = BookData.builder()
                .name(name)
                .author(author)
                .genre(genreData)
                .build();

        bookRepository.save(bookData);

        BookData bookData2 = BookData.builder()
                .name(name+"2")
                .author(author)
                .genre(genreData)
                .build();

        bookRepository.save(bookData2);

        mvc.perform(post("/api/books/_search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        )
              .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",
                        is(2)))
                .andExpect(jsonPath("$[0].genre.name",
                        is(genreData.getName())));

    }

    @Test
    public void testSearch_notExist() throws Exception {
        String body = """
          {
              "genre": "%s"
          }
        """.formatted("not exist");

        BookData bookData = BookData.builder()
                .name(name)
                .author(author)
                .genre(genreData)
                .build();

        bookRepository.save(bookData);

        BookData bookData2 = BookData.builder()
                .name(name+"2")
                .author(author)
                .genre(genreData)
                .build();

        bookRepository.save(bookData2);

        mvc.perform(post("/api/books/_search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
              .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",
                        is(0)));

    }

    @Test
    public void testGetAllGenre() throws Exception {
        mvc.perform(get("/api/genres"))
              .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",
                        is(5)));

    }

    @Test
    public void testDeleteBook() throws Exception {

        BookData bookData = BookData.builder()
                .name(name)
                .author(author)
                .genre(genreData)
                .build();

        int bookId = bookRepository.save(bookData);
        MvcResult mvcResult = mvc.perform(delete("/api/books/delete/{id}" , bookId))
                .andReturn();


        String result = mvcResult.getResponse().getContentAsString();
        assertEquals(result, "The book with id " + bookId + " is deleted.");
        assertNull(bookRepository.getBookById(bookId));

    }

    @Test
    public void testDeleteBook_notFound() throws Exception {
        BookData bookData = BookData.builder()
                .name(name)
                .author(author)
                .genre(genreData)
                .build();

        int bookId = bookRepository.save(bookData);
        mvc.perform(delete("/api/books/delete/{id}" , bookId+1))
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertEquals("Book with id %d not found".formatted(bookId+1), result.getResolvedException().getMessage()));
    }

    @Test
    public void testDeleteAllBook() throws Exception {
        BookData bookData = BookData.builder()
                .name(name)
                .author(author)
                .genre(genreData)
                .build();

        bookRepository.save(bookData);
        MvcResult mvcResult = mvc.perform(delete("/api/books/deleteAll" ))
                .andReturn();


        String result = mvcResult.getResponse().getContentAsString();
        assertEquals(result, "All books are deleted.");

        BookQueryDto queryDto = new BookQueryDto();
        List<BookData> allBook = bookRepository.getAllBook(queryDto.getAllParams());
        assertEquals(allBook.size(), 0);
    }

}
