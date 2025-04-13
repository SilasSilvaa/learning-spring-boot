package com.ssilvadev.api.service;

import static com.ssilvadev.api.mapper.ObjectMapper.parseObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.ssilvadev.api.Repository.PersonRepository;
import com.ssilvadev.api.controllers.PersonController;
import com.ssilvadev.api.data.dto.PersonDTO;
import com.ssilvadev.api.exception.RequiredObjectsIsNullException;
import com.ssilvadev.api.exception.ResourceNotFoundException;
import com.ssilvadev.api.model.Person;

import jakarta.transaction.Transactional;

@Service
public class PersonServices {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PagedResourcesAssembler<PersonDTO> assembler;

    private Logger logger = LoggerFactory.getLogger(PersonServices.class.getName());

    public PagedModel<EntityModel<PersonDTO>> findAll(Pageable pageable) {
        logger.info("Finding all people");

        var people = personRepository.findAll(pageable);
        var result = people.map(person -> {
            var dto = parseObject(person, PersonDTO.class);
            addHateoasLinks(dto);
            return dto;
        });

        // Links HAL
        Link findAllLinks = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder
                .methodOn(PersonController.class)
                .findAll(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        String.valueOf(pageable.getSort())))
                .withSelfRel();

        return assembler.toModel(result, findAllLinks);
    }

    public PagedModel<EntityModel<PersonDTO>> findByName(String firstName, Pageable pageable) {
        logger.info("Finding people by name");

        var people = personRepository.findPeopleByName(firstName, pageable);

        var result = people.map(person -> {
            var dto = parseObject(person, PersonDTO.class);
            addHateoasLinks(dto);
            return dto;
        });

        Link findAllLinks = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder
                .methodOn(PersonController.class)
                .findAll(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        String.valueOf(pageable.getSort())))
                .withSelfRel();

        return assembler.toModel(result, findAllLinks);
    }

    public PersonDTO findById(Long id) {
        logger.info("Finding one Person!");

        var entity = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));

        var dto = parseObject(entity, PersonDTO.class);
        addHateoasLinks(dto);

        return dto;
    }

    public PersonDTO create(PersonDTO person) {
        if (person == null) {
            throw new RequiredObjectsIsNullException();
        }
        logger.info("Creating one person");

        Person entity = parseObject(person, Person.class);

        var dto = parseObject(personRepository.save(entity), PersonDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public PersonDTO update(PersonDTO person) {
        if (person == null) {
            throw new RequiredObjectsIsNullException();
        }

        logger.info("Updating one person");

        Person entity = personRepository.findById(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        var dto = parseObject(personRepository.save(entity), PersonDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public void delete(Long id) {
        logger.info("Deleting one Person!");

        Person entity = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));

        personRepository.delete(entity);
    }

    @Transactional
    public PersonDTO disablePerson(Long id) {
        logger.info("Disabling one Person!");

        personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));

        personRepository.disablePerson(id);

        var entity = personRepository.findById(id).get();

        PersonDTO dto = parseObject(entity, PersonDTO.class);
        addHateoasLinks(dto);

        return dto;
    }

    private void addHateoasLinks(PersonDTO dto) {
        dto.add(linkTo(methodOn(PersonController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).findAll(0, 12, "asc")).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        dto.add(linkTo(methodOn(PersonController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(PersonController.class).disable(dto.getId())).withRel("disable").withType("PATCH"));
        dto.add(linkTo(methodOn(PersonController.class).create(dto)).withRel("update").withType("PUT"));
    }
}
