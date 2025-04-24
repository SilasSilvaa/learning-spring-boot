package com.ssilvadev.api.service;

import static com.ssilvadev.api.mapper.ObjectMapper.parseObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import com.ssilvadev.api.Repository.PersonRepository;
import com.ssilvadev.api.controllers.PersonController;
import com.ssilvadev.api.data.dto.PersonDTO;
import com.ssilvadev.api.exception.BadRequestException;
import com.ssilvadev.api.exception.FileStorageException;
import com.ssilvadev.api.exception.RequiredObjectsIsNullException;
import com.ssilvadev.api.exception.ResourceNotFoundException;
import com.ssilvadev.api.file.exporter.contract.FileExporter;
import com.ssilvadev.api.file.exporter.factory.FileExporterFactory;
import com.ssilvadev.api.file.importer.factory.FileImporterFactory;
import com.ssilvadev.api.model.Person;

import jakarta.transaction.Transactional;

@Service
public class PersonServices {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PagedResourcesAssembler<PersonDTO> assembler;

    @Autowired
    private FileImporterFactory importer;

    @Autowired
    private FileExporterFactory exporter;

    private Logger logger = LoggerFactory.getLogger(PersonServices.class.getName());

    public PagedModel<EntityModel<PersonDTO>> findAll(Pageable pageable) {
        logger.info("Finding all people");

        var people = personRepository.findAll(pageable);

        return buildPagedModel(pageable, people);
    }

    public PagedModel<EntityModel<PersonDTO>> findByName(String firstName, Pageable pageable) {
        logger.info("Finding people by name");

        var people = personRepository.findPeopleByName(firstName, pageable);

        return buildPagedModel(pageable, people);
    }

    public PersonDTO findById(Long id) {
        logger.info("Finding one Person!");

        var entity = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));

        var dto = parseObject(entity, PersonDTO.class);
        addHateoasLinks(dto);

        return dto;
    }

    public Resource exportPage(Pageable pageable, String acceptHeader) {
        logger.info("Exporting a people page");

        var people = personRepository.findAll(pageable)
                .map(p -> parseObject(p, PersonDTO.class))
                .getContent();

        try {
            FileExporter exporterFile = this.exporter.getExporter(acceptHeader);

            return exporterFile.exportFile(people);
        } catch (Exception e) {
            throw new RuntimeException("Error while exporting file");
        }
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

    public List<PersonDTO> massCreation(MultipartFile file) {
        logger.info("Importing people from file");

        if (file.isEmpty()) {
            throw new BadRequestException("Please set a valid file!");
        }

        try (InputStream inputStream = file.getInputStream()) {
            var filename = Optional.ofNullable(file.getOriginalFilename())
                    .orElseThrow(() -> new BadRequestException("File name cannot be null"));

            var importer = this.importer.getImporter(filename);

            var entities = importer.importFile(inputStream).stream()
                    .map(dto -> personRepository.save(parseObject(dto, Person.class)))
                    .toList();

            var result = entities.stream().map(entity -> {
                var dto = parseObject(entity, PersonDTO.class);
                addHateoasLinks(dto);
                return dto;
            }).toList();

            return result;
        } catch (Exception e) {
            throw new FileStorageException("Error processing the file!");
        }
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

    private PagedModel<EntityModel<PersonDTO>> buildPagedModel(Pageable pageable, Page<Person> people) {
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

    private void addHateoasLinks(PersonDTO dto) {
        dto.add(linkTo(methodOn(PersonController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).findAll(0, 12, "asc")).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).findByName("", 0, 12, "asc")).withRel("findByName")
                .withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class)).slash("massCreation").withRel("massCreation")
                .withType("POST"));
        dto.add(linkTo(methodOn(PersonController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        dto.add(linkTo(methodOn(PersonController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(PersonController.class).disable(dto.getId())).withRel("disable").withType("PATCH"));
        dto.add(linkTo(methodOn(PersonController.class).create(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(PersonController.class).exportPage(0, 12, "asc", null)).withRel("update")
                .withType("GET"));
    }
}
