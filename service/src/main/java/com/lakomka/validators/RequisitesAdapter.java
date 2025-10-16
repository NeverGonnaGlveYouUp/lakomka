package com.lakomka.validators;

import com.lakomka.dto.RegistrationDto;
import com.lakomka.validators.RequisitesValidator.CompanyRequisites;
import com.lakomka.validators.RequisitesValidator.IndividualRequisites;

/**
 * Адаптер для преобразования RegistrationDto в объекты реквизитов
 */
public class RequisitesAdapter {

    /**
     * Создает объект реквизитов на основе DTO
     */
    public static Object createRequisites(RegistrationDto dto) {
        if (dto.isJuridical()) {
            return createCompanyRequisites(dto);
        } else if (dto.isIndividual()) {
            return createIndividualRequisites(dto);
        } else {
            throw new IllegalArgumentException("Неизвестный тип организации. ИНН должен содержать 10 или 12 цифр");
        }
    }

    /**
     * Создает реквизиты для юридического лица
     */
    public static CompanyRequisites createCompanyRequisites(RegistrationDto dto) {
        if (!dto.isJuridical()) {
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
    public static IndividualRequisites createIndividualRequisites(RegistrationDto dto) {
        if (!dto.isIndividual()) {
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
    public static boolean invalidOrganizationType(RegistrationDto dto) {
        return !dto.isJuridical() && !dto.isIndividual();
    }
}
