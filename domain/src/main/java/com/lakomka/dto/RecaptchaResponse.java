package com.lakomka.dto;

import lombok.Data;

import java.util.List;

@Data
public class RecaptchaResponse {
    private boolean success;
    private String challenge_ts;
    private String hostname;
    private double score;
    private String action;
    private List<String> errorCodes;

}