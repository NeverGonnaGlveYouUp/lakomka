package com.lakomka.dto.security;

import com.lakomka.models.person.BasePerson;

public class AuthenticationRequest {

    private String login;
    private String password;

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
}