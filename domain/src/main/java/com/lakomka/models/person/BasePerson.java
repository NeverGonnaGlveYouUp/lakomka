package com.lakomka.models.person;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lakomka.dto.RegistrationDto;
import com.lakomka.models.order.Order;
import com.lakomka.models.product.PersonCartItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table
@Entity
@Getter
@Setter
public class BasePerson implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, precision = 12)
    private Long id;

    @Column(name = "login", unique = true)
    private String login;

    @Column(name = "password")
    private String password;

    @Transient
    private String repeatPassword;

    @JsonIgnore
    @OneToMany(mappedBy = "basePerson")
    private Set<Order> orders = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "basePerson")
    private Set<PersonCartItem> cart = new HashSet<>();

    @JsonIgnore
    @PrimaryKeyJoinColumn
    @OneToOne(mappedBy = "basePerson", cascade = CascadeType.ALL)
    private JPerson jPerson;

    @JsonIgnore
    @PrimaryKeyJoinColumn
    @OneToOne(mappedBy = "basePerson", cascade = CascadeType.ALL)
    private Person person;

    public BasePerson() {
    }

    public BasePerson(RegistrationDto registrationDto) {
        this.login = registrationDto.getLogin();
        this.password = registrationDto.getPassword();
        this.repeatPassword = registrationDto.getRepeatPassword();
    }

    public JPerson getjPerson() {
        return jPerson;
    }

    public void setjPerson(JPerson jPerson) {
        this.jPerson = jPerson;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
