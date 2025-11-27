package com.lakomka.repository.order;

import com.lakomka.models.order.Order;
import com.lakomka.models.order.OrderItem;
import com.lakomka.models.person.BasePerson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findAllByOrder(Order order);

    List<OrderItem> findAllByIdAndBasePerson(Long id, BasePerson basePerson);
}
