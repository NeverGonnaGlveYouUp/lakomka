package com.lakomka.models.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lakomka.models.order.Order;
import com.lakomka.models.product.PersonCartItem;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Table
@Entity
public class BasePerson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, precision = 12)
    private Long id;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @Transient
    private String repeatPassword;

    @JsonIgnore
    @OneToMany(mappedBy="basePerson")
    private Set<Order> orders = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "basePerson")
    private Set<PersonCartItem> cart = new HashSet<>();

    @PrimaryKeyJoinColumn
    @OneToOne(mappedBy = "basePerson", cascade = CascadeType.ALL)
    private JPerson jPerson;

    @PrimaryKeyJoinColumn
    @OneToOne(mappedBy = "basePerson", cascade = CascadeType.ALL)
    private Person person;

    public BasePerson() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public Set<PersonCartItem> getCart() {
        return cart;
    }

    public void setCart(Set<PersonCartItem> cart) {
        this.cart = cart;
    }

    public JPerson getjPerson() {
        return jPerson;
    }

    public void setjPerson(JPerson jPerson) {
        this.jPerson = jPerson;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
