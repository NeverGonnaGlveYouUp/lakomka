package com.lakomka.validators;

import com.lakomka.dto.CreateJPersonDto;
import com.lakomka.dto.RegistrationDto;
import com.lakomka.dtoAssemblers.RequisitesDtoAssembler;
import com.lakomka.validators.RequisitesValidator.CompanyRequisites;
import com.lakomka.validators.RequisitesValidator.IndividualRequisites;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("validatorRegistrationDto")
@RequiredArgsConstructor
public class RegistrationValidator implements Validator {

    private final RequisitesAdapter createCompanyRequisites;

    private final RequisitesDtoAssembler registrationDtoAssembler;

    private final RequisitesValidator requisitesValidator;

    @Override
    public boolean supports(Class<?> clazz) {
        return RegistrationDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CreateJPersonDto dto = (CreateJPersonDto) target;

        // Базовая проверка обязательных полей
        validateRequiredFields(dto, errors);

        // Если есть ошибки в базовых полях, не проверяем реквизиты
        if (errors.hasErrors()) {
            return;
        }

        // Проверяем тип организации по ИНН
        if (createCompanyRequisites.invalidOrganizationType(dto)) {
            errors.rejectValue("inn", "inn.invalid.length",
                    "ИНН должен содержать 10 цифр (для юрлица) или 12 цифр (для ИП)");
            return;
        }

        // Валидируем реквизиты в зависимости от типа организации
        try {
            if (registrationDtoAssembler.isJuridical(dto)) {
                validateJuridicalRequisites(dto, errors);
            } else if (registrationDtoAssembler.isIndividual(dto)) {
                validateIndividualRequisites(dto, errors);
            }
        } catch (IllegalArgumentException e) {
            errors.reject("requisites.invalid", e.getMessage());
        }

        // Дополнительные проверки бизнес-логики
        validateBusinessRules(dto, errors);
    }

    private void validateRequiredFields(CreateJPersonDto dto, Errors errors) {
        if (dto.getInn() == null || dto.getInn().trim().isEmpty()) {
            errors.rejectValue("inn", "inn.required", "ИНН является обязательным полем");
        }

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            errors.rejectValue("name", "name.required", "Наименование является обязательным полем");
        }

        if (dto.getOgrn() == null || dto.getOgrn().trim().isEmpty()) {
            errors.rejectValue("ogrn", "ogrn.required", "ОГРН/ОГРНИП является обязательным полем");
        }

        // Для юрлиц КПП обязателен
        if (registrationDtoAssembler.isJuridical(dto) && (dto.getKpp() == null || dto.getKpp().trim().isEmpty())) {
            errors.rejectValue("kpp", "kpp.required", "КПП является обязательным для юридических лиц");
        }
    }

    private void validateJuridicalRequisites(CreateJPersonDto dto, Errors errors) {
        CompanyRequisites requisites = createCompanyRequisites.createCompanyRequisites(dto);

        // Валидируем ИНН юрлица
        requisitesValidator.validateInnJuridical(requisites.getInn(), "inn", errors);

        // Валидируем ОГРН
        requisitesValidator.validateOgrn(requisites.getOgrn(), "ogrn", errors);

        // Валидируем КПП
        if (requisites.getKpp() != null) {
            requisitesValidator.validateKpp(requisites.getKpp(), "kpp", errors);
        }

        // Дополнительная проверка: для юрлица ОГРН должен быть 13 цифр
        if (requisites.getOgrn() != null && requisites.getOgrn().length() != 13) {
            errors.rejectValue("ogrn", "ogrn.invalid.length.juridical",
                    "ОГРН юридического лица должен содержать 13 цифр");
        }
    }

    private void validateIndividualRequisites(CreateJPersonDto dto, Errors errors) {
        IndividualRequisites requisites = createCompanyRequisites.createIndividualRequisites(dto);

        // Валидируем ИНН физлица
        requisitesValidator.validateInnIndividual(requisites.getInn(), "inn", errors);

        // Валидируем ОГРНИП
        requisitesValidator.validateOgrnip(requisites.getOgrnip(), "ogrn", errors);

        // Дополнительная проверка: для ИП ОГРН должен быть 15 цифр
        if (requisites.getOgrnip() != null && requisites.getOgrnip().length() != 15) {
            errors.rejectValue("ogrn", "ogrnip.invalid.length.individual",
                    "ОГРНИП должен содержать 15 цифр");
        }

        // Для ИП КПП должен быть null или пустым
        if (dto.getKpp() != null && !dto.getKpp().trim().isEmpty()) {
            errors.rejectValue("kpp", "kpp.invalid.individual",
                    "КПП не должен указываться для ИП/физлиц");
        }
    }

    private void validateBusinessRules(CreateJPersonDto dto, Errors errors) {
        // Проверка формата телефона
        if (dto.getPhone() != null && !dto.getPhone().trim().isEmpty()) {
            if (!isValidPhone(dto.getPhone())) {
                errors.rejectValue("phone", "phone.invalid.format",
                        "Номер телефона имеет неверный формат");
            }
        }

        // Проверка, что юридический адрес заполнен для юрлиц
        if (registrationDtoAssembler.isJuridical(dto) &&
                (dto.getJurAddress() == null || dto.getJurAddress().trim().isEmpty())) {
            errors.rejectValue("jurAddress", "jurAddress.required",
                    "Юридический адрес обязателен для юридических лиц");
        }
    }

    private boolean isValidPhone(String phone) {
        // Простая проверка формата телефона
        return phone.matches("^[\\d\\+\\(\\)\\-\\s]{5,20}$");
    }

    /**
     * Валидация только реквизитов (ИНН, ОГРН, КПП)
     */
    public void validateRequisitesOnly(CreateJPersonDto dto, Errors errors) {
        if (createCompanyRequisites.invalidOrganizationType(dto)) {
            errors.rejectValue("inn", "inn.invalid.length",
                    "ИНН должен содержать 10 цифр (для юрлица) или 12 цифр (для ИП)");
            return;
        }

        try {
            if (registrationDtoAssembler.isJuridical(dto)) {
                validateJuridicalRequisites(dto, errors);
            } else if (registrationDtoAssembler.isIndividual(dto)) {
                validateIndividualRequisites(dto, errors);
            }
        } catch (IllegalArgumentException e) {
            errors.reject("requisites.invalid", e.getMessage());
        }
    }
}
