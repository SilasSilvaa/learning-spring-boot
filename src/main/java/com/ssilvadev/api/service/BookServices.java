package com.ssilvadev.api.service;

import static com.ssilvadev.api.mapper.ObjectMapper.parseListObjects;
import static com.ssilvadev.api.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssilvadev.api.Repository.BookRepository;
import com.ssilvadev.api.controllers.BookController;
import com.ssilvadev.api.data.dto.BookDTO;
import com.ssilvadev.api.exception.RequiredObjectsIsNullException;
import com.ssilvadev.api.exception.ResourceNotFoundException;
import com.ssilvadev.api.model.Book;

@Service
public class BookServices {

    @Autowired
    private BookRepository repository;

    private Logger logger = LoggerFactory.getLogger(PersonServices.class.getName());

    public List<BookDTO> findAll() {
        logger.info("Get all books");

        var entity = repository.findAll();
        var response = parseListObjects(entity, BookDTO.class);
        response.forEach(this::addHateoasLinks);

        return response;
    }

    public BookDTO findById(Long id) {
        logger.info("Find book by id");

        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));

        System.out.println(entity.toString());
        var response = parseObject(entity, BookDTO.class);
        addHateoasLinks(response);

        return response;
    }

    public BookDTO create(BookDTO book) {

        if (book == null) throw new RequiredObjectsIsNullException();

        logger.info("Creating one Book!");
        var entity = parseObject(book, Book.class);

        var dto = parseObject(repository.save(entity), BookDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public BookDTO update(BookDTO book) {
        if (book == null) {
            throw new RequiredObjectsIsNullException();
        }

        logger.info("Updating one book");

        Book entity = repository.findById(book.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));

        entity.setAuthor(book.getAuthor());
        entity.setLaunchDate(book.getLaunchDate());
        entity.setPrice(book.getPrice());
        entity.setTitle(book.getTitle());

        var dto = parseObject(repository.save(entity), BookDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public void delete(Long id) {
        logger.info("Deleting an book");

        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));

        repository.delete(entity);
    }

    private void addHateoasLinks(BookDTO dto) {
        dto.add(linkTo(methodOn(BookController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(BookController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(BookController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        // dto.add(linkTo(methodOn(BookController.class).create(dto)).withRel("create").withType("POST"));
        // dto.add(linkTo(methodOn(BookController.class).create(dto)).withRel("update").withType("PUT"));
    }

}
