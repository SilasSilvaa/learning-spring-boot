package com.ssilvadev.api.service;

import java.util.List;

import static com.ssilvadev.api.mapper.ObjectMapper.parseListObjects;
import static com.ssilvadev.api.mapper.ObjectMapper.parseObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.ssilvadev.api.Repository.PersonRepository;
import com.ssilvadev.api.controllers.PersonController;
import com.ssilvadev.api.data.dto.PersonDTO;
import com.ssilvadev.api.exception.RequiredObjectsIsNullException;
import com.ssilvadev.api.exception.ResourceNotFoundException;
import com.ssilvadev.api.model.Person;

@Service
public class PersonServices {

    @Autowired
    private PersonRepository personRepository;

    private Logger logger = LoggerFactory.getLogger(PersonServices.class.getName());

    public List<PersonDTO> findAll() {
        logger.info("Finding all people");
        var people = parseListObjects(personRepository.findAll(), PersonDTO.class);
        people.forEach(this::addHateoasLinks);

        return people;
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

    private void addHateoasLinks(PersonDTO dto) {
        dto.add(linkTo(methodOn(PersonController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        dto.add(linkTo(methodOn(PersonController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(PersonController.class).create(dto)).withRel("update").withType("PUT"));
    }
}
