package com.lakomka.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public class LoggedUser {

    public String userName;
    public String name;
    public String nameFull;
    public String address;
    public String OGRN;
    public String INN;
    public String KPP;
    public String phone;
    public String email;
    public String contact;
    public String post;
    public String addressDelivery;
    public String mapDelivery;
    public BigDecimal rest;
    public BigDecimal restTime;

}
