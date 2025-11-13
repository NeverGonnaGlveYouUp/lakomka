package com.lakomka.models.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lakomka.dto.AuthenticationRequest;
import com.lakomka.dto.RegistrationDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.List;

@Table
@Entity
@Getter
@Setter
//@ToString(exclude = {"password", "repeatPassword", "person", "jPerson", "cart", "orders"})
@ToString(exclude = {"password", "repeatPassword", "person", "jPerson"})
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

//    @JsonIgnore
//    @OneToMany(mappedBy = "basePerson")
//    private Set<Order> orders = new HashSet<>();

//    @JsonIgnore
//    @OneToMany(mappedBy = "basePerson")
//    private Set<PersonCartItem> cart = new HashSet<>();

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

    public BasePerson(PasswordEncoder passwordEncoder, RegistrationDto registrationDto) {
        this.login = registrationDto.getLogin();
        String encoded = passwordEncoder.encode(registrationDto.getPassword());
        this.password = encoded;
        this.repeatPassword = encoded;
    }

    public BasePerson(PasswordEncoder passwordEncoder, AuthenticationRequest authenticationRequest) {
        this.login = authenticationRequest.getLogin();
        String encoded = passwordEncoder.encode(authenticationRequest.getPassword());
        this.password = encoded;
        this.repeatPassword = encoded;
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
