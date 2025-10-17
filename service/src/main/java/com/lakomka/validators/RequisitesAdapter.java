package com.lakomka.validators;

import com.lakomka.dto.RegistrationDto;
import com.lakomka.dtoAssemblers.RegistrationDtoAssembler;
import com.lakomka.validators.RequisitesValidator.CompanyRequisites;
import com.lakomka.validators.RequisitesValidator.IndividualRequisites;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Адаптер для преобразования RegistrationDto в объекты реквизитов
 */
@Component
public class RequisitesAdapter {

    @Autowired
    private RegistrationDtoAssembler registrationDtoAssembler;

    /**
     * Создает объект реквизитов на основе DTO
     */
    public Object createRequisites(RegistrationDto dto) {
        if (registrationDtoAssembler.isJuridical(dto)) {
            return createCompanyRequisites(dto);
        } else if (registrationDtoAssembler.isIndividual(dto)) {
            return createIndividualRequisites(dto);
        } else {
            throw new IllegalArgumentException("Неизвестный тип организации. ИНН должен содержать 10 или 12 цифр");
        }
    }

    /**
     * Создает реквизиты для юридического лица
     */
    public CompanyRequisites createCompanyRequisites(RegistrationDto dto) {
        if (!registrationDtoAssembler.isJuridical(dto)) {
            throw new IllegalArgumentException("DTO не содержит данные юридического лица");
        }

        return new CompanyRequisites(
                dto.getInn(),
                dto.getOgrn(),
                dto.getKpp()
        );
    }

    /**
     * Создает реквизиты для ИП/физлица
     */
    public IndividualRequisites createIndividualRequisites(RegistrationDto dto) {
        if (!registrationDtoAssembler.isIndividual(dto)) {
            throw new IllegalArgumentException("DTO не содержит данные ИП/физлица");
        }

        return new IndividualRequisites(
                dto.getInn(),
                dto.getOgrn() // Для ИП это будет ОГРНИП
        );
    }

    /**
     * Проверяет, валиден ли тип организации в DTO
     */
    public boolean invalidOrganizationType(RegistrationDto dto) {
        return !registrationDtoAssembler.isJuridical(dto) && !registrationDtoAssembler.isIndividual(dto);
    }
}
