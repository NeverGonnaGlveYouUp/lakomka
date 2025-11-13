package com.lakomka.repository.person;

import com.lakomka.models.person.BasePerson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BasePersonRepository extends JpaRepository<BasePerson, Long> {
    Optional<BasePerson> findByLogin(String login);
}
