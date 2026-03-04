package com.lakomka.models.person;

import com.lakomka.dto.AuthenticationRequest;
import com.lakomka.dto.LoggedUser;
import com.lakomka.dto.RegistrationDto;
import com.lakomka.models.misc.Route;
import com.lakomka.repository.person.BasePersonRepository;
import com.lakomka.repository.person.JPersonRepository;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Table
@Entity
@Getter
@Setter
@ToString(exclude = {"password", "repeatPassword", "person", "jPersons"})
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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "current_j_person_id")
    private JPerson currentJPerson;

    @OneToMany(mappedBy = "basePerson", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JPerson> jPersons = new ArrayList<>();

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

    public void addJPerson(JPerson jPerson) {
        jPerson.setBasePerson(this);
        jPersons.add(jPerson);
    }

    public List<LoggedUser> jPersonsToListDto(){
        return jPersons.stream().map(jPerson -> LoggedUser.builder()
                .name(jPerson.getName())
                .nameFull(jPerson.getNameFull())
                .address(jPerson.getAddress())
                .OGRN(jPerson.getOGRN())
                .INN(jPerson.getINN())
                .KPP(jPerson.getKPP())
                .phone(jPerson.getPhone())
                .email(jPerson.getEmail())
                .contact(jPerson.getContact())
                .post(jPerson.getPost())
                .addressDelivery(jPerson.getAddressDelivery())
                .mapDelivery(jPerson.getMapDelivery())
                .rest(jPerson.getRest())
                .restTime(jPerson.getRestTime())
                .route(Optional.ofNullable(jPerson.getRoute()).orElseGet(Route::new).getRouteString())
                .build()).toList();
    }

}
