package com.lakomka.models.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lakomka.models.misc.Discount;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Table
@Entity
public class ProductGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, precision = 12)
    private Long id;

    @JsonIgnore
    @OneToMany(mappedBy = "productGroup")
    private Set<Discount> discounts = new HashSet<>();

    @Column(name = "product_group_name", length = 50)
    private String name;

    @OneToMany(mappedBy = "group")
    private Set<Product> products;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Discount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(Set<Discount> discounts) {
        this.discounts = discounts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public Set<Product> getProduct() {
        return products;
    }

    public void setProduct(Set<Product> products) {
        this.products = products;
    }
}
