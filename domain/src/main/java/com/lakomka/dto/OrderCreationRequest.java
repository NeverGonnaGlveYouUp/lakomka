package com.lakomka.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
public class OrderCreationRequest {

    private String contact;
    private String telephone;
    private String email;
    private String addressDelivery;
    private String prim;

    private Date dateDelivery;

    private boolean bitAccPrint;
    private boolean bitSertifPrint;
    private boolean payVid;

}