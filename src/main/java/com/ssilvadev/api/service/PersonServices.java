package com.ssilvadev.api.service;

import java.util.List;

import static com.ssilvadev.api.mapper.ObjectMapper.parseListObjects;
import static com.ssilvadev.api.mapper.ObjectMapper.parseObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssilvadev.api.Repository.PersonRepository;
import com.ssilvadev.api.data.dto.PersonDTO;
import com.ssilvadev.api.exception.ResourceNotFoundException;
import com.ssilvadev.api.model.Person;

@Service
public class PersonServices {

    @Autowired
    private PersonRepository personRepository;

    private Logger logger = LoggerFactory.getLogger(PersonServices.class.getName());

    public List<PersonDTO> findAll() {
        logger.info("Finding all people");
        return parseListObjects(personRepository.findAll(), PersonDTO.class);
    }

    public PersonDTO findById(Long id) {
        logger.info("Finding one Person!");

        var entity = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));

        return parseObject(entity, PersonDTO.class);
    }

    public PersonDTO create(PersonDTO person) {
        logger.info("Creating one person");
        Person entity = parseObject(person, Person.class);

        return parseObject(personRepository.save(entity), PersonDTO.class);
    }

    public PersonDTO update(PersonDTO person) {
        logger.info("Updating one person");

        Person entity = personRepository.findById(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        return parseObject(personRepository.save(entity), PersonDTO.class);
    }

    public void delete(Long id) {
        logger.info("Deleting one Person!");

        Person entity = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));

        personRepository.delete(entity);
    }
}
