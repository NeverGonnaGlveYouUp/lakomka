package com.lakomka.models;

import jakarta.persistence.*;

/**
 * Сущность ФЛ Покупателя
 */
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

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
}
