package com.example.hw5;

import com.example.hw5.data.BookData;
import com.example.hw5.data.GenreData;
import com.example.hw5.dto.BookDetailsDto;
import com.example.hw5.dto.BookQueryDto;
import com.example.hw5.dto.RestResponse;
import com.example.hw5.exception.NotFoundException;
import com.example.hw5.repository.BookRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private ObjectMapper objectMapper;

    @AfterEach
    public void afterEach() {
        bookRepository.deleteAll();
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
//        String name = "book1";
//        String author = "Author";
        int genreId = 2;
        String body = """
          {
              "name": "%s",
              "author": "%s",
              "genreId": %d
          }
        """.formatted(name, author, genreId);
        MvcResult mvcResult = mvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
                .andExpect(status().isCreated())
                .andReturn();

        RestResponse response = parseResponse(mvcResult, RestResponse.class);
        int bookId = Integer.parseInt(response.getResult());
        assertThat(bookId).isGreaterThanOrEqualTo(1);

        BookData book = bookRepository.getBookById(bookId);
        assertThat(book).isNotNull();
        assertThat(book.getName()).isEqualTo(name);
        assertThat(book.getAuthor()).isEqualTo(author);
        assertThat(book.getGenre().getId()).isEqualTo(genreId);
    }

    @Test
    public void testCreateBook_validation() throws Exception {
        mvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetBook() throws Exception {
//        String name = "book1";
//        String author = "Author";
//        GenreData genreData = new GenreData(2,"historical");

//        BookData bookData = new BookData();
//        bookData.setName(name);
//        bookData.setAuthor(author);
//        bookData.setGenre(genreData);


        BookData bookData = BookData.builder()
                .name(name)
                .author(author)
                .genre(genreData)
                .build();

        int bookId = bookRepository.save(bookData);
        MvcResult mvcResult = mvc.perform(get("/api/books/{id}" , bookId)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andReturn();

        BookDetailsDto response = parseResponse(mvcResult, BookDetailsDto.class);
        assertThat(response).isNotNull();
        assertThat(response.getId()).isGreaterThanOrEqualTo(1);
        assertThat(response.getAuthor()).isEqualTo(author);
        assertThat(response.getGenre().getName()).isEqualTo(genreData.getName());
        assertThat(response.getGenre().getId()).isEqualTo(genreData.getId());
    }

    @Test
    public void testGetBook_notFoundException() throws Exception {
//        String name = "book1";
//        String author = "Author";
//        GenreData genreData = new GenreData(2,"historical");
//
        BookData bookData = BookData.builder()
                .name(name)
                .author(author)
                .genre(genreData)
                .build();


        int bookId = bookRepository.save(bookData);
        mvc.perform(get("/api/books/{id}" , bookId+1)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertEquals("Book with id %d not found".formatted(bookId+1), result.getResolvedException().getMessage()));
    }



    @Test
    public void testUpdateBook() throws Exception {
//        String name = "book1";
        String nameNew = "bookAfterChange";
//        String author = "Author";
//        GenreData genreData = new GenreData(2,"historical");
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

        RestResponse response = parseResponse(mvcResult, RestResponse.class);
        String result = (response.getResult());
        assertThat(result).isEqualTo("OK");

        BookData book = bookRepository.getBookById(bookId);
        assertThat(book).isNotNull();
        assertThat(book.getName()).isEqualTo(nameNew);
        assertThat(book.getId()).isEqualTo(bookId);
        assertThat(book.getAuthor()).isEqualTo(author);
        assertThat(book.getGenre().getId()).isEqualTo(genreData.getId());
    }

    @Test
    public void testUpdateBook_notFoundException() throws Exception {
//        String name = "book1";
//        String author = "Author";
//        GenreData genreData = new GenreData(2,"historical");
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
//        String name = "book1";
        String nameNew = "bookNew";
//        String author = "Author";
//        GenreData genreData = new GenreData(2,"historical");
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

        RestResponse response = parseResponse(mvcResult, RestResponse.class);
        String result = (response.getResult());
        assertThat(result).isEqualTo("OK");

        BookData book = bookRepository.getBookById(bookId);
        assertThat(book).isNotNull();
        assertThat(book.getName()).isEqualTo(nameNew);
        assertThat(book.getId()).isEqualTo(bookId);
        assertThat(book.getAuthor()).isEqualTo(author);
        assertThat(book.getGenre().getId()).isEqualTo(genreData.getId());
    }

    @Test
    public void testSearch_bookName() throws Exception {
//        String name = "book";
//        String author = "Author";
//        GenreData genreData = new GenreData(2,"historical");
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
                .andExpect(jsonPath("$[0].nameAndAuthor",
                        is(name + " " + author)));

    }

    @Test
    public void testSearch_idGenre() throws Exception {
//        String name = "book";
//        String author = "Author";
//        GenreData genreData = new GenreData(2,"historical");
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
                .andExpect(jsonPath("$[0].genre",
                        is(genreData.getName())));

    }


    @Test
    public void testSearch_emptyBody() throws Exception {
//        String name = "book";
//        String author = "Author";
//        GenreData genreData = new GenreData(2,"historical");

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
                .andExpect(jsonPath("$[0].genre",
                        is(genreData.getName())));

    }

    @Test
    public void testSearch_notExist() throws Exception {
//        String name = "book";
//        String author = "Author";
//        GenreData genreData = new GenreData(2,"historical");
        String body = """
          {
              "genreId": "%d"
          }
        """.formatted(100);

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

    //todo
    @Test
    public void testGetAllGenre() throws Exception {


        mvc.perform(get("/api/books/genre")
                .contentType(MediaType.APPLICATION_JSON)
        )
              .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",
                        is(5)));

    }

    @Test
    public void testGetGenreBooks() throws Exception {
//        String name = "book";
//        String author = "Author";
//        GenreData genreData = new GenreData(2,"historical");

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

        mvc.perform(get("/api/books/genre/{id}", genreData.getId())
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",
                        is(2)))
                .andExpect(jsonPath("$[0].genre",
                        is(genreData.getName())));

    }

    @Test
    public void testDeleteBook() throws Exception {
//        String name = "book1";
//        String author = "Author";
//        GenreData genreData = new GenreData(2,"historical");

        BookData bookData = BookData.builder()
                .name(name)
                .author(author)
                .genre(genreData)
                .build();

        int bookId = bookRepository.save(bookData);
        MvcResult mvcResult = mvc.perform(delete("/api/books/delete/{id}" , bookId)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andReturn();


        RestResponse response = parseResponse(mvcResult, RestResponse.class);
        String result = (response.getResult());
        assertThat(result).isEqualTo("OK");

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookRepository.getBookById(bookId));
        assertEquals("Book with id %d not found".formatted(bookId), exception.getMessage());

    }

    @Test
    public void testDeleteBook_notFoundException() throws Exception {
//        String name = "book1";
//        String author = "Author";
//        GenreData genreData = new GenreData(2,"historical");

        BookData bookData = BookData.builder()
                .name(name)
                .author(author)
                .genre(genreData)
                .build();

        int bookId = bookRepository.save(bookData);
        mvc.perform(delete("/api/books/delete/{id}" , bookId+1)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertEquals("Book with id %d not found".formatted(bookId+1), result.getResolvedException().getMessage()));
    }

    @Test
    public void testDeleteAllBook() throws Exception {
//        String name = "book1";
//        String author = "Author";
//        GenreData genreData = new GenreData(2,"historical");

        BookData bookData = BookData.builder()
                .name(name)
                .author(author)
                .genre(genreData)
                .build();

        int bookId = bookRepository.save(bookData);
        MvcResult mvcResult = mvc.perform(delete("/api/books/deleteAll" )
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andReturn();


        RestResponse response = parseResponse(mvcResult, RestResponse.class);
        String result = (response.getResult());
        assertThat(result).isEqualTo("OK");

        BookQueryDto queryDto = new BookQueryDto();
        List<BookData> allBook = bookRepository.getAllBook(queryDto);
        assertEquals(allBook.size(), 0);

//        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookRepository.getAllBook(queryDto));
//        assertEquals("Book with id %d not found".formatted(bookId), exception.getMessage());

    }

}
