package com.lakomka.repository;

import com.lakomka.models.JPerson;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Репозиторий ЮЛ покупателя
 */
public interface JPersonRepository extends JpaRepository<JPerson, Long> {
}