package com.ssilvadev.annotationsAndExceptions.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;
import com.ssilvadev.annotationsAndExceptions.model.Person;

@Service
public class PersonServices {

    private final AtomicLong counter = new AtomicLong();
    private Logger logger = Logger.getLogger(PersonServices.class.getName());

    public Person findById(String id) {
        logger.info("Finding one Person!");

        Person person = new Person();
        person.setId(counter.incrementAndGet());
        person.setFirstName("Silas");
        person.setLastName("Silva");
        person.setAddress("São Paulo");
        person.setGender("Male");

        return person;
    }

    public List<Person> findAll() {
        logger.info("Finding all People!");

        var persons = new ArrayList<Person>();

        for (int i = 0; i < 8; i++) {
            Person person = mockPerson(i);
            persons.add(person);
        }

        return persons;
    }

    public Person create(Person person) {
        logger.info("Creating one person");

        return person;
    }

    public Person update(Person person) {
        logger.info("Updating one person");

        return person;
    }

    public void delete(String id) {
        logger.info("Deleting one Person!");

        Person person = new Person();
        person.setId(counter.incrementAndGet());
        person.setFirstName("Silas");
        person.setLastName("Silva");
        person.setAddress("São Paulo");
        person.setGender("Male");

    }

    private Person mockPerson(int i) {
        Person person = new Person();
        person.setId(counter.incrementAndGet());
        person.setFirstName("Name " + i);
        person.setLastName("Last Name " + i);
        person.setAddress("Address " + i);
        person.setGender("Gender" + i);

        return person;
    }
}
