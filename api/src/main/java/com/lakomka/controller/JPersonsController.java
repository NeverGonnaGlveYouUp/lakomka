package com.lakomka.controller;

import com.lakomka.dto.CreateJPersonDto;
import com.lakomka.dto.LoggedUser;
import com.lakomka.dtoAssemblers.RequisitesDtoAssembler;
import com.lakomka.models.person.BasePerson;
import com.lakomka.models.person.JPerson;
import com.lakomka.repository.person.BasePersonRepository;
import com.lakomka.repository.person.JPersonRepository;
import com.lakomka.services.xml.exports.JPersonExport;
import com.lakomka.validators.RegistrationValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

@RestController
@RequestMapping("/api/jpersons")
@RequiredArgsConstructor
@Slf4j
public class JPersonsController {

    private final JPersonRepository jPersonRepository;
    private final JPersonExport jPersonExport;
    private final RequisitesDtoAssembler registrationDtoAssembler;
    private final BasePersonRepository basePersonRepository;
    private final RegistrationValidator registrationValidator;

    /**
     * Export all JPersons to Xml file on S3 storage
     *
     * @param user - user
     * @return - exported file name if success, null if fail
     */
    @GetMapping("/export-to-s3")
    @Transactional
    public ResponseEntity<?> exportXml(
            @AuthenticationPrincipal BasePerson user
    ) {
        if (isNull(user)) {
            return ResponseEntity.ok(false);
        }

        return ResponseEntity.ok(jPersonExport.safeExportXml(jPersonRepository.findAll()));

    }

    @GetMapping("/get-by-user")
    public ResponseEntity<List<LoggedUser>> getAllJPersons(
            @AuthenticationPrincipal BasePerson user
    ) {
        if (isNull(user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<LoggedUser> jPersonsDots = user.getJPersons().stream().map(
                jPerson -> LoggedUser.builder()
                        .currentJPersonId(jPerson.getId())
                        .address(jPerson.getAddress())
                        .KPP(jPerson.getKPP())
                        .build()
                ).toList();

        jPersonsDots.forEach(jPersonSwitchDto -> log.info("User id: {} requested its short jPersons: {} ", user.getId(), jPersonSwitchDto.toString()));
        return ResponseEntity.ok(jPersonsDots);
    }

    @PostMapping("/set-current-jperson/{id}")
    public ResponseEntity<?> setCurrentJPerson(
            @AuthenticationPrincipal BasePerson user,
            @PathVariable Long id
    ) {
        Optional<JPerson> jPersonOptional = user.getJPersons().stream().filter(jPerson -> jPerson.getId().equals(id)).findFirst();

        if (jPersonOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        user.setCurrentJPerson(jPersonOptional.get());
        basePersonRepository.save(user);

        log.info("User id: {} sets new currentJPerson id: {} ", user.getId(), jPersonOptional.get().getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create-j-person")
    public ResponseEntity<?> createJPerson(
            @AuthenticationPrincipal BasePerson user,
            @Valid @RequestBody CreateJPersonDto createJPersonDto
    ) {
        createJPersonDto.setInn(createJPersonDto.getInn().replaceAll("-", ""));

        Errors errors = new BeanPropertyBindingResult(createJPersonDto, "createJPersonDto");
        registrationValidator.validateRequisitesOnly(createJPersonDto, errors);

        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }

        Object jPersonObj = registrationDtoAssembler.toEntity(createJPersonDto);
        assert jPersonObj instanceof JPerson;
        JPerson jPerson = (JPerson) jPersonObj;

        user.addJPerson(jPerson);
        if (user.getCurrentJPerson() == null) {
            user.setCurrentJPerson(jPerson);
        }
        basePersonRepository.save(user);

        log.info("User id: {} created new JPerson id: {} ", user.getId(), jPerson.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
