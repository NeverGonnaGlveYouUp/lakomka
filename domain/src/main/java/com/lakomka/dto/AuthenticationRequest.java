package com.lakomka.dto;

import com.lakomka.models.person.BasePerson;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthenticationRequest {

    private String login;
    private String password;
    private String siteKey;
    private String token;
    private String expectedAction;

    public AuthenticationRequest() {
    }

    public AuthenticationRequest(String username, String password) {
        this.login = username;
        this.password = password;
    }

    public BasePerson createBasePerson() {
        BasePerson basePerson = new BasePerson();

        basePerson.setLogin(this.login);
        basePerson.setPassword(this.password);

        return basePerson;
    }

}