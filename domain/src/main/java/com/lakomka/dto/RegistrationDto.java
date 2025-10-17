package com.lakomka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDto {
    private String login;
    private String password;
    private String repeatPassword;
    private String inn;
    private String kpp;
    private String ogrn;
    private String deliveryAddress;
    private String jurAddress;
    private String name;
    private String nameFull;
    private String contact;
    private String phone;
    private boolean dpAgreement;
}
