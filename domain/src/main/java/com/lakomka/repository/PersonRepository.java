package com.lakomka.repository;

import com.lakomka.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий ЮЛ Покупателя
 */
public interface PersonRepository extends JpaRepository<Person, Long> {
}