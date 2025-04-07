package com.ssilvadev.api.integrationtests.controllers.withXml;

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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.ssilvadev.api.config.TestConfigs;
import com.ssilvadev.api.integrationtests.dto.PersonDTO;
import com.ssilvadev.api.integrationtests.testcontainers.AbstractIntegrationTest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerXmlTest extends AbstractIntegrationTest {
    private static RequestSpecification specification;
    private static ObjectMapper mapper;

    private static PersonDTO person;

    @BeforeAll
    static void setUp() {
        mapper = new XmlMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        person = new PersonDTO();
    }

    @Test
    @Order(1)
    void testCreate() throws JsonProcessingException{
        mockPerson();

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_1)
                .addHeader("Accept", "application/xml")
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var content = given(specification)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .body(person)
                .when()
                .post()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_XML_VALUE)
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
				.contentType(MediaType.APPLICATION_XML_VALUE)
				.body(person)
				.when()
				.put()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_XML_VALUE)
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
				.contentType(MediaType.APPLICATION_XML_VALUE)
				.pathParam("id", person.getId())
				.when()
				.get("{id}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_XML_VALUE)
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
				.contentType(MediaType.APPLICATION_XML_VALUE)
				.pathParam("id", person.getId())
				.when()
				.patch("{id}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_XML_VALUE)
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
				.accept(MediaType.APPLICATION_XML_VALUE)
				.when()
				.get()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_XML_VALUE)
				.extract()
				.body()
				.asString();

		List<PersonDTO> people = mapper.readValue(content, new TypeReference<List<PersonDTO>>() {
		});

		PersonDTO personOne = people.get(0);

		assertNotNull(personOne.getId());
		assertTrue(personOne.getId() > 0);

		assertEquals("Some Name 15", personOne.getFirstName());
		assertEquals("Some LastName", personOne.getLastName());
		assertEquals("São Paulo", personOne.getAddress());
		assertEquals("Male", personOne.getGender());
		assertTrue(personOne.getEnabled());

		PersonDTO personFour = people.get(4);
		assertNotNull(personFour.getId());
		assertTrue(personFour.getId() > 0);

		assertEquals("Some Name 15", personFour.getFirstName());
		assertEquals("Some LastName", personFour.getLastName());
		assertEquals("São Paulo", personFour.getAddress());
		assertEquals("Male", personFour.getGender());
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
