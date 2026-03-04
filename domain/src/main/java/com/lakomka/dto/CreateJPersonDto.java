package com.lakomka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateJPersonDto {
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
    private String token;
    private String expectedAction;
    private String siteKey;
}