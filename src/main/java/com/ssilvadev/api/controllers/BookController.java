package com.ssilvadev.api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssilvadev.api.controllers.docs.BookControllerDocs;
import com.ssilvadev.api.data.dto.BookDTO;
import com.ssilvadev.api.service.BookServices;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/books/v1")
public class BookController implements BookControllerDocs {

        @Autowired
        private BookServices service;

        @GetMapping(produces = {
                        MediaType.APPLICATION_JSON_VALUE,
                        MediaType.APPLICATION_XML_VALUE,
                        MediaType.APPLICATION_YAML_VALUE
        })
        @Override
        public ResponseEntity<PagedModel<EntityModel<BookDTO>>> findAll(
                        @RequestParam(value = "page", defaultValue = "0") Integer page,
                        @RequestParam(value = "size", defaultValue = "12") Integer size,
                        @RequestParam(value = "direction", defaultValue = "asc") String direction) {

                var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
                Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "title"));

                return ResponseEntity.ok(service.findAll(pageable));
        }

        @GetMapping(value = "/{id}", produces = {
                        MediaType.APPLICATION_JSON_VALUE,
                        MediaType.APPLICATION_XML_VALUE,
                        MediaType.APPLICATION_YAML_VALUE })
        public BookDTO findById(@PathVariable Long id) {
                return service.findById(id);
        }

        @PostMapping(produces = {
                        MediaType.APPLICATION_JSON_VALUE,
                        MediaType.APPLICATION_XML_VALUE,
                        MediaType.APPLICATION_YAML_VALUE }, consumes = {
                                        MediaType.APPLICATION_JSON_VALUE,
                                        MediaType.APPLICATION_XML_VALUE,
                                        MediaType.APPLICATION_YAML_VALUE
                        })
        @Override
        public BookDTO create(@RequestBody BookDTO book) {
                return service.create(book);
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
        public BookDTO update(@RequestBody BookDTO book) {
                return service.update(book);
        }

        @DeleteMapping(value = "/{id}", produces = {
                        MediaType.APPLICATION_JSON_VALUE,
                        MediaType.APPLICATION_XML_VALUE,
                        MediaType.APPLICATION_YAML_VALUE })
        @Override
        public ResponseEntity<?> delete(@PathVariable Long id) {
                service.delete(id);

                return ResponseEntity.noContent().build();
        }
}
