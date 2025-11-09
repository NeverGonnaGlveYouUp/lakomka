package com.lakomka.repository.order;

import com.lakomka.models.order.Order;
import com.lakomka.models.person.BasePerson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByGuest(String guest);

    List<Order> findAllByBasePerson(BasePerson basePerson);

    Optional<Order> findByIdAndGuest(Long id, String guest);

    Optional<Order> findByIdAndBasePerson(Long id, BasePerson basePerson);
}