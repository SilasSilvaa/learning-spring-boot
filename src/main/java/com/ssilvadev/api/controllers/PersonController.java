package com.ssilvadev.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssilvadev.api.controllers.docs.PersonControllerDocs;
import com.ssilvadev.api.data.dto.PersonDTO;
import com.ssilvadev.api.file.exporter.MediaTypes;
import com.ssilvadev.api.service.PersonServices;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

// @CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api/person/v1")
@Tag(name = "People", description = "Endpoints for mapping People")
public class PersonController implements PersonControllerDocs {
	@Autowired
	private PersonServices service;

	@GetMapping(produces = {
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_YAML_VALUE })
	@Override
	public ResponseEntity<PagedModel<EntityModel<PersonDTO>>> findAll(
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "size", defaultValue = "12") Integer size,
			@RequestParam(value = "direction", defaultValue = "asc") String direction) {

		var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;

		Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
		return ResponseEntity.ok(service.findAll(pageable));
	}

	@GetMapping(value = "/findPeopleByName/{firstName}", produces = {
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_YAML_VALUE })
	@Override
	public ResponseEntity<PagedModel<EntityModel<PersonDTO>>> findByName(
			@PathVariable("firstName") String firstName,
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "size", defaultValue = "12") Integer size,
			@RequestParam(value = "direction", defaultValue = "asc") String direction) {

		var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;

		Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
		return ResponseEntity.ok(service.findByName(firstName, pageable));
	}

	// @CrossOrigin(origins = "http://localhost:8080")
	@GetMapping(value = "/{id}", produces = {
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_YAML_VALUE })

	@Override
	public PersonDTO findById(@PathVariable Long id) {
		return service.findById(id);
	}

	@PostMapping(value = "/massCreation", produces = {
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_YAML_VALUE
	})
	@Override
	public List<PersonDTO> massCreation(@RequestParam(name = "file") MultipartFile file) {
		return service.massCreation(file);
	}

	@Override
	@GetMapping(value = "/exportPage", produces = {
			"application/vnd.openxml-formats-officedocument.spreadsheetnk.sheet",
			"text/csv"
	})
	public ResponseEntity<Resource> exportPage(
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "size", defaultValue = "12") Integer size,
			@RequestParam(value = "direction", defaultValue = "asc") String direction,
			HttpServletRequest request) {

		var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;

		Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));

		var acceptHeader = request.getHeader(HttpHeaders.ACCEPT);

		Resource file = service.exportPage(pageable, acceptHeader);

		var contentType = acceptHeader != null ? acceptHeader : "application/octet-strem";
		var fileExtension = MediaTypes.APPLICATION_XLSX_VALUE.getMediaType()
				.equalsIgnoreCase(acceptHeader) ? ".xlsx" : ".csv";

		var fileName = "people_export" + fileExtension;

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
				.body(file);
	}

	// @CrossOrigin(origins = {"http://localhost:8080", "http://localhost:8181"})
	@PostMapping(produces = {
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_YAML_VALUE }, consumes = {
					MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_YAML_VALUE
			})

	@Override
	public PersonDTO create(@RequestBody PersonDTO person) {
		return service.create(person);
	}

	@PutMapping(produces = {
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_YAML_VALUE }, consumes = {
					MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_YAML_VALUE
			})
	@Override
	public PersonDTO update(@RequestBody PersonDTO person) {
		return service.update(person);
	}

	@PatchMapping(value = "/{id}", produces = {
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_YAML_VALUE })

	@Override
	public PersonDTO disable(@PathVariable Long id) {
		return service.disablePerson(id);
	}

	@DeleteMapping(value = "/{id}")
	@Override
	public ResponseEntity<?> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
