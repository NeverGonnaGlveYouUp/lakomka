package com.lakomka.repository.person;

import com.lakomka.models.person.JPerson;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Репозиторий ЮЛ покупателя
 */
public interface JPersonRepository extends JpaRepository<JPerson, Long> {
}