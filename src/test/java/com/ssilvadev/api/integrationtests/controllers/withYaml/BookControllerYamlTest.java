package com.ssilvadev.api.integrationtests.controllers.withYaml;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import com.ssilvadev.api.config.TestConfigs;
import com.ssilvadev.api.integrationtests.controllers.withYaml.mapper.YAMLMapper;
import com.ssilvadev.api.integrationtests.dto.BookDTO;
import com.ssilvadev.api.integrationtests.dto.wrappers.xml.PagedModelBook;
import com.ssilvadev.api.integrationtests.testcontainers.AbstractIntegrationTest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerYamlTest extends AbstractIntegrationTest {

	private static RequestSpecification specification;
	private static YAMLMapper mapper;

	private static BookDTO book;

	@BeforeAll
	static void setUp() {
		mapper = new YAMLMapper();

		book = new BookDTO();
	}

	@Test
	@Order(1)
	void testCreate() {
		mockBook();

		specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_1)
				.addHeader("Accept", MediaType.APPLICATION_YAML_VALUE)
				.setBasePath("/api/books/v1")
				.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();

		var createdBook = given()
				.config(RestAssuredConfig.config()
						.encoderConfig(
								EncoderConfig.encoderConfig()
										.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
				.spec(specification)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.body(book, mapper)
				.when()
				.post()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.as(BookDTO.class, mapper);

		book = createdBook;

		assertNotNull(book.getId());
		assertTrue(book.getId() > 0);

		assertEquals("Michael C. Feathers", createdBook.getAuthor());
		assertNotNull(createdBook.getLaunchDate());
		assertEquals(Double.valueOf(49.90), createdBook.getPrice());
		assertEquals("Working effectively with legacy code", createdBook.getTitle());
	}

	@Test
	@Order(2)
	void testUpdate() {
		book.setAuthor("Michael C.");

		var updateBook = given()
				.config(RestAssuredConfig.config()
						.encoderConfig(
								EncoderConfig.encoderConfig()
										.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
				.spec(specification)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.body(book, mapper)
				.when()
				.put()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.as(BookDTO.class, mapper);

		book = updateBook;

		assertNotNull(updateBook.getId());
		assertTrue(updateBook.getId() > 0);

		assertEquals("Michael C.", updateBook.getAuthor());
		assertNotNull(updateBook.getLaunchDate());
		assertEquals(Double.valueOf(49.90), updateBook.getPrice());
		assertEquals("Working effectively with legacy code", updateBook.getTitle());
	}

	@Test
	@Order(3)
	void testFindById() {
		var foundBook = given()
				.config(RestAssuredConfig.config()
						.encoderConfig(
								EncoderConfig.encoderConfig()
										.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
				.spec(specification)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.pathParam("id", book.getId())
				.when()
				.get("{id}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.as(BookDTO.class, mapper);

		assertNotNull(foundBook.getId());
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
	void testFindAll() {
		var content = given(specification)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.queryParam(
						"page", 1,
						"size", 12,
						"direction", "asc")
				.when()
				.get()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.as(PagedModelBook.class, mapper);

		var books = content.getContent();

		var bookOne = books.get(0);

		assertNotNull(bookOne.getId());
		assertTrue(bookOne.getId() > 0);

		assertEquals("Marc J. Schiller", bookOne.getAuthor());
		assertNotNull(bookOne.getLaunchDate());
		assertEquals(Double.valueOf(45.0), bookOne.getPrice());
		assertEquals("Os 11 segredos de líderes de TI altamente influentes", bookOne.getTitle());

		var bookThree = books.get(2);

		assertNotNull(bookThree.getId());
		assertNotNull(bookThree.getId() > 0);

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
