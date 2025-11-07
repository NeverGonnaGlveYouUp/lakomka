package com.lakomka.repository.order;

import com.lakomka.models.order.Order;
import com.lakomka.models.person.BasePerson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByGuest(String guest);

    List<Order> findAllByBasePerson(BasePerson basePerson);
}