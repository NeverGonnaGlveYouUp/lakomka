package com.lakomka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RecaptchaResponse {
    private boolean success;
    private String challenge_ts;
    private String hostname;
    private double score;
    private String action;
    private List<String> errorCodes;

}