package com.lakomka.services;

import com.lakomka.models.person.BasePerson;
import com.lakomka.repository.person.BasePersonRepository;
import com.lakomka.validators.BasePersonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private BasePersonRepository basePersonRepository;

    @Autowired
    private BasePersonValidator validator;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<BasePerson> user = basePersonRepository.findByLogin(login);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        return user.get();
    }
}