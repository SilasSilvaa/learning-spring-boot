package com.ssilvadev.annotationsAndExceptions.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssilvadev.annotationsAndExceptions.model.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

}
