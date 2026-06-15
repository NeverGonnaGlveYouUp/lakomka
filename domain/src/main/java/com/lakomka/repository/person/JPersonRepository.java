package com.lakomka.repository.person;

import com.lakomka.models.person.JPerson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/**
 * Репозиторий ЮЛ покупателя
 */
public interface JPersonRepository extends JpaRepository<JPerson, Long> {
    Optional<JPerson> findByKpp(String kpp);
}