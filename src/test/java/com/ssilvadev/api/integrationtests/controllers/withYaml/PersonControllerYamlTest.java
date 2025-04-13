package com.ssilvadev.api.integrationtests.controllers.withYaml;

import static io.restassured.RestAssured.given;
import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssilvadev.api.config.TestConfigs;
import com.ssilvadev.api.integrationtests.controllers.withYaml.mapper.YAMLMapper;
import com.ssilvadev.api.integrationtests.dto.PersonDTO;
import com.ssilvadev.api.integrationtests.dto.wrappers.xml.PagedModelPerson;
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
class PersonControllerYamlTest extends AbstractIntegrationTest {
	private static RequestSpecification specification;
	private static YAMLMapper mapper;

	private static PersonDTO person;

	@BeforeAll
	static void setUp() {
		mapper = new YAMLMapper();

		person = new PersonDTO();
	}

	@Test
	@Order(1)
	void testCreate() {
		mockPerson();

		specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_1)
				.addHeader("Accept", MediaType.APPLICATION_YAML_VALUE)
				.setBasePath("/api/person/v1")
				.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();

		var createdPerson = given()
				.config(RestAssuredConfig.config()
						.encoderConfig(
								EncoderConfig.encoderConfig()
										.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
				.spec(specification)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.body(person, mapper)
				.when()
				.post()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.as(PersonDTO.class, mapper);

		person = createdPerson;

		assertNotNull(createdPerson.getId());
		assertTrue(createdPerson.getId() > 0);

		assertEquals("Linus", createdPerson.getFirstName());
		assertEquals("Torvalds", createdPerson.getLastName());
		assertEquals("Helsinki - Finland", createdPerson.getAddress());
		assertEquals("Male", createdPerson.getGender());
		assertTrue(createdPerson.getEnabled());
	}

	@Test
	@Order(2)
	void testUpdate() throws JsonProcessingException {

		person.setLastName("Benedict Torvals");

		var createdPerson = given()
				.config(RestAssuredConfig.config()
						.encoderConfig(
								EncoderConfig.encoderConfig()
										.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
				.spec(specification)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.body(person, mapper)
				.when()
				.put()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.as(PersonDTO.class, mapper);

		person = createdPerson;

		assertNotNull(createdPerson.getId());
		assertTrue(createdPerson.getId() > 0);

		assertEquals("Linus", createdPerson.getFirstName());
		assertEquals("Benedict Torvals", createdPerson.getLastName());
		assertEquals("Helsinki - Finland", createdPerson.getAddress());
		assertEquals("Male", createdPerson.getGender());
		assertTrue(createdPerson.getEnabled());
	}

	@Test
	@Order(3)
	void testFindById() throws JsonProcessingException {
		var createdPerson = given()
				.config(RestAssuredConfig.config()
						.encoderConfig(
								EncoderConfig.encoderConfig()
										.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
				.spec(specification)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.pathParam("id", person.getId())
				.when()
				.get("{id}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.as(PersonDTO.class, mapper);

		person = createdPerson;

		assertNotNull(createdPerson.getId());
		assertTrue(createdPerson.getId() > 0);

		assertEquals("Linus", createdPerson.getFirstName());
		assertEquals("Benedict Torvals", createdPerson.getLastName());
		assertEquals("Helsinki - Finland", createdPerson.getAddress());
		assertEquals("Male", createdPerson.getGender());
		assertTrue(createdPerson.getEnabled());
	}

	@Test
	@Order(4)
	void testDisable() throws JsonProcessingException {
		var createdPerson = given()
				.config(RestAssuredConfig.config()
						.encoderConfig(
								EncoderConfig.encoderConfig()
										.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
				.spec(specification)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.pathParam("id", person.getId())
				.when()
				.patch("{id}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.as(PersonDTO.class, mapper);

		person = createdPerson;

		assertNotNull(createdPerson.getId());
		assertTrue(createdPerson.getId() > 0);

		assertEquals("Linus", createdPerson.getFirstName());
		assertEquals("Benedict Torvals", createdPerson.getLastName());
		assertEquals("Helsinki - Finland", createdPerson.getAddress());
		assertEquals("Male", createdPerson.getGender());
		assertFalse(createdPerson.getEnabled());
	}

	@Test
	@Order(5)
	void testDelete() throws JsonProcessingException {
		given(specification)
				.pathParam("id", person.getId())
				.when()
				.delete("{id}")
				.then()
				.statusCode(204);

	}

	@Test
	@Order(6)
	void testFindAll() throws JsonProcessingException {
		var response = given(specification)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.queryParam(
						"page", 3,
						"size", 12,
						"direction", "asc")
				.when()
				.get()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.as(PagedModelPerson.class, mapper);

		List<PersonDTO> people = response.getContent();

		PersonDTO personOne = people.get(0);

		assertNotNull(personOne.getId());
		assertTrue(personOne.getId() > 0);

		assertEquals("Alford", personOne.getFirstName());
		assertEquals("Flett", personOne.getLastName());
		assertEquals("Suite 34", personOne.getAddress());
		assertEquals("Male", personOne.getGender());
		assertTrue(personOne.getEnabled());

		PersonDTO personFour = people.get(4);
		assertNotNull(personFour.getId());
		assertTrue(personFour.getId() > 0);

		assertEquals("Alic", personFour.getFirstName());
		assertEquals("Pidgin", personFour.getLastName());
		assertEquals("Apt 755", personFour.getAddress());
		assertEquals("Male", personFour.getGender());
		assertTrue(personFour.getEnabled());
	}

	@Test
	@Order(7)
	void testFindByName() {
		var response = given(specification)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.pathParam("firstName", "and")
				.queryParam(
						"page", 1,
						"size", 12,
						"direction", "asc")
				.when()
				.get("findPeopleByName/{firstName}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.as(PagedModelPerson.class, mapper);

		List<PersonDTO> people = response.getContent();

		PersonDTO personOne = people.get(0);

		assertNotNull(personOne.getId());
		assertTrue(personOne.getId() > 0);

		assertEquals("Candice", personOne.getFirstName());
		assertEquals("Linforth", personOne.getLastName());
		assertEquals("1st Floor", personOne.getAddress());
		assertEquals("Female", personOne.getGender());
		assertTrue(personOne.getEnabled());

		PersonDTO personFour = people.get(4);
		assertNotNull(personFour.getId());
		assertTrue(personFour.getId() > 0);

		assertEquals("Cassandry", personFour.getFirstName());
		assertEquals("Nadin", personFour.getLastName());
		assertEquals("PO Box 66875", personFour.getAddress());
		assertEquals("Female", personFour.getGender());
		assertTrue(personFour.getEnabled());
	}

	private void mockPerson() {
		person.setFirstName("Linus");
		person.setLastName("Torvalds");
		person.setAddress("Helsinki - Finland");
		person.setGender("Male");
		person.setEnabled(true);

	}
}
