package com.lakomka.validators;

import com.lakomka.dto.CreateJPersonDto;
import com.lakomka.dtoAssemblers.RequisitesDtoAssembler;
import com.lakomka.validators.RequisitesValidator.CompanyRequisites;
import com.lakomka.validators.RequisitesValidator.IndividualRequisites;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Адаптер для преобразования RegistrationDto в объекты реквизитов
 */
@Component
@RequiredArgsConstructor
public class RequisitesAdapter {

    private final RequisitesDtoAssembler registrationDtoAssembler;

    /**
     * Создает объект реквизитов на основе DTO
     */
    public Object createRequisites(CreateJPersonDto dto) {
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
    public CompanyRequisites createCompanyRequisites(CreateJPersonDto dto) {
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
    public IndividualRequisites createIndividualRequisites(CreateJPersonDto dto) {
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
    public boolean invalidOrganizationType(CreateJPersonDto dto) {
        return !registrationDtoAssembler.isJuridical(dto) && !registrationDtoAssembler.isIndividual(dto);
    }
}
