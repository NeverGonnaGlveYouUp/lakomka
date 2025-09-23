package com.lakomka.models.person;

import jakarta.persistence.*;

/**
 * Сущность ФЛ Покупателя
 */

@Table
@Entity
public class Person {

    @Id
    @Column(name = "base_person_id")
    private Long id;

    @OneToOne(cascade = CascadeType.MERGE)
    @MapsId
    @JoinColumn(name = "base_person_id")
    private BasePerson basePerson;

    /**
     * Согласие на обработку ПД
     */
    @Column(name = "dp_agreement")
    private boolean dpAgreement = false;

    /**
     * Имя Покупателя
     */
    @Column(name = "name", length = 50)
    private String name;

    /**
     * Фамилия Покупателя
     */
    @Column(name = "surname", length = 50)
    private String surname;

    /**
     * Полный реестровый юридический адрес Покупателя
     */
    @Column(name = "address")
    private String address;

    /**
     * Контактный телефон
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * Электронная почта
     */
    @Column(name = "email", length = 50)
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BasePerson getBasePerson() {
        return basePerson;
    }

    public void setBasePerson(BasePerson basePerson) {
        this.basePerson = basePerson;
    }

    public boolean isDpAgreement() {
        return dpAgreement;
    }

    public void setDpAgreement(boolean dpAgreement) {
        this.dpAgreement = dpAgreement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
