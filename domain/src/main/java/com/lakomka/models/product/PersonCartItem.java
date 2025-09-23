package com.lakomka.models.product;

import com.lakomka.models.person.BasePerson;
import jakarta.persistence.*;

@Table
@Entity
public class PersonCartItem {

    @Id
    Long id;

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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BasePerson getBasePerson() {
        return basePerson;
    }

    public void setBasePerson(BasePerson basePerson) {
        this.basePerson = basePerson;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}