package com.lakomka.repository.person;

import com.lakomka.models.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий ФЛ Покупателя
 */
public interface PersonRepository extends JpaRepository<Person, Long> {
}