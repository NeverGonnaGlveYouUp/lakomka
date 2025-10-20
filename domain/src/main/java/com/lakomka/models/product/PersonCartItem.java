package com.lakomka.models.product;

import com.lakomka.dto.CartItemDto;
import com.lakomka.models.person.BasePerson;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Optional;

@Setter
@Getter
@Table
@Entity
public class PersonCartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "base_person_id")
    private BasePerson basePerson;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;

    public PersonCartItem() {
    }

    public PersonCartItem(BasePerson basePerson, Product product, Integer quantity) {
        this.basePerson = basePerson;
        this.product = product;
        this.quantity = quantity;
    }

    public CartItemDto toCartItemDto(){
        return new CartItemDto(
            product.getId(),
            product.getName(),
            product.getPriceKons().toPlainString(),
            quantity
        );
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PersonCartItem cartItem = (PersonCartItem) o;
        return Objects.equals(getId(), cartItem.getId()) &&
               Objects.equals(
                       Optional.ofNullable(getBasePerson()).map(BasePerson::getId).orElse(null),
                       Optional.ofNullable(cartItem.getBasePerson()).map(BasePerson::getId).orElse(null)) &&
               Objects.equals(getProduct().getId(), cartItem.getProduct().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), Optional.ofNullable(getBasePerson()).map(BasePerson::getId).orElse(null), getProduct().getId());
    }
}
