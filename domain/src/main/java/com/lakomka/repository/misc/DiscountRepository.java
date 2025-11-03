package com.lakomka.repository.misc;

import com.lakomka.models.misc.Discount;
import com.lakomka.models.person.JPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface DiscountRepository extends JpaRepository<Discount, Long> {

    @Query("SELECT d FROM Discount d WHERE d.jPerson = :jPerson")
    Set<Discount> findAllByJPerson(@Param("jPerson") JPerson jPerson);

}