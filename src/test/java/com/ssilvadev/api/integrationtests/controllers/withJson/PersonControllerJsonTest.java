package com.ssilvadev.api.integrationtests.controllers.withJson;

import com.ssilvadev.api.config.TestConfigs;
import com.ssilvadev.api.integrationtests.dto.PersonDTO;
import com.ssilvadev.api.integrationtests.dto.wrappers.json.WrapperPersonDTO;
import com.ssilvadev.api.integrationtests.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerJsonTest extends AbstractIntegrationTest {

	private static RequestSpecification specification;
	private static ObjectMapper mapper;

	private static PersonDTO person;

	@BeforeAll
	static void setUp() {
		mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		person = new PersonDTO();
	}

	@Test
	@Order(1)
	void testCreate() throws JsonProcessingException {
		mockPerson();

		specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_1)
				.setBasePath("/api/person/v1")
				.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();

		var content = given(specification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(person)
				.when()
				.post()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.extract()
				.body()
				.asString();

		PersonDTO createdPerson = mapper.readValue(content, PersonDTO.class);
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

		var content = given(specification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(person)
				.when()
				.put()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.extract()
				.body()
				.asString();

		PersonDTO createdPerson = mapper.readValue(content, PersonDTO.class);
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
		var content = given(specification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.pathParam("id", person.getId())
				.when()
				.get("{id}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.extract()
				.body()
				.asString();

		PersonDTO createdPerson = mapper.readValue(content, PersonDTO.class);
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
		var content = given(specification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.pathParam("id", person.getId())
				.when()
				.patch("{id}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.extract()
				.body()
				.asString();

		PersonDTO createdPerson = mapper.readValue(content, PersonDTO.class);
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
		var content = given(specification)
				.accept(MediaType.APPLICATION_JSON_VALUE)
				.queryParam(
						"page", 3,
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

		WrapperPersonDTO wrapper = mapper.readValue(content, WrapperPersonDTO.class);

		List<PersonDTO> people = wrapper.getEmbedded().getPeople();
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
	void testFindByName() throws JsonProcessingException {
		var content = given(specification)
				.accept(MediaType.APPLICATION_JSON_VALUE)
				.pathParam("firstName", "and")
				.queryParam(
						"page", 1,
						"size", 12,
						"direction", "asc")
				.get("findPeopleByName/{firstName}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.extract()
				.body()
				.asString();

		WrapperPersonDTO wrapper = mapper.readValue(content, WrapperPersonDTO.class);

		List<PersonDTO> people = wrapper.getEmbedded().getPeople();
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
