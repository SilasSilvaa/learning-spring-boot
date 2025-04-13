package com.ssilvadev.api.integrationtests.controllers.withJson;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssilvadev.api.config.TestConfigs;
import com.ssilvadev.api.integrationtests.dto.BookDTO;
import com.ssilvadev.api.integrationtests.dto.wrappers.json.WrapperBookDTO;
import com.ssilvadev.api.integrationtests.testcontainers.AbstractIntegrationTest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper mapper;

    private static BookDTO book;

    @BeforeAll
    static void setUp() {
        mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        book = new BookDTO();
    }

    @Test
    @Order(1)
    void testCreate() throws JsonProcessingException {
        mockBook();

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_1)
                .setBasePath("/api/books/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var contet = given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(book)
                .when()
                .post()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .body()
                .asString();

        var createdBook = mapper.readValue(contet, BookDTO.class);
        book = createdBook;

        assertNotNull(createdBook.getId());
        assertTrue(createdBook.getId() > 0);

        assertEquals("Michael C. Feathers", createdBook.getAuthor());
        assertEquals(Double.valueOf(49.90), createdBook.getPrice());
        assertNotNull(book.getLaunchDate());
        assertEquals("Working effectively with legacy code", createdBook.getTitle());

    }

    @Test
    @Order(2)
    void testUpdate() throws JsonProcessingException {
        book.setAuthor("Michael C.");

        var content = given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(book)
                .when()
                .put()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .body()
                .asString();

        var updatedBook = mapper.readValue(content, BookDTO.class);

        assertNotNull(updatedBook.getId());
        assertTrue(updatedBook.getId() > 0);

        assertEquals("Michael C.", updatedBook.getAuthor());
        assertEquals(Double.valueOf(49.90), updatedBook.getPrice());
        assertNotNull(book.getLaunchDate());
        assertEquals("Working effectively with legacy code", updatedBook.getTitle());
    }

    @Test
    @Order(3)
    void testFindById() throws JsonProcessingException {
        var content = given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", book.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .body()
                .asString();

        var findBook = mapper.readValue(content, BookDTO.class);

        assertNotNull(book.getId());
        assertTrue(book.getId() > 0);

        assertEquals("Michael C.", findBook.getAuthor());
        assertEquals(Double.valueOf(49.90), findBook.getPrice());
        assertNotNull(book.getLaunchDate());
        assertEquals("Working effectively with legacy code", findBook.getTitle());
    }

    @Test
    @Order(4)
    void testDelete() {
        given(specification)
                .pathParam("id", book.getId())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(5)
    void testFindAll() throws JsonProcessingException {
        var content = given(specification)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParam(
                        "page", 1,
                        "size", 12,
                        "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .body()
                .asString();

        WrapperBookDTO wrapper = mapper.readValue(content, WrapperBookDTO.class);
        var books = wrapper.getEmbedded().getBooks();

        var bookOne = books.get(0);

        assertNotNull(bookOne.getId());
        assertTrue(bookOne.getId() > 0);

        assertEquals("Marc J. Schiller", bookOne.getAuthor());
        assertNotNull(bookOne.getLaunchDate());
        assertEquals(Double.valueOf(45.0), bookOne.getPrice());
        assertEquals("Os 11 segredos de líderes de TI altamente influentes", bookOne.getTitle());

        var bookThree = books.get(2);

        assertNotNull(bookThree.getId());
        assertTrue(bookThree.getId() > 0);

        assertEquals("Michael C. Feathers", bookThree.getAuthor());
        assertNotNull(bookThree.getLaunchDate());
        assertEquals(Double.valueOf(49.0), bookThree.getPrice());
        assertEquals("Working effectively with legacy code", bookThree.getTitle());
    }

    private static void mockBook() {
        book.setAuthor("Michael C. Feathers");
        book.setLaunchDate(new Date());
        book.setPrice(49.90);
        book.setTitle("Working effectively with legacy code");
    }
}
