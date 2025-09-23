package com.lakomka.repository.person;

import com.lakomka.models.person.BasePerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;

@RestResource(exported = false)
public interface BasePersonRepository extends JpaRepository<BasePerson, Long> {
    Optional<BasePerson> findByLogin(String login);
}
