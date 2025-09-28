package com.lakomka.repository.misc;

import com.lakomka.models.misc.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
}