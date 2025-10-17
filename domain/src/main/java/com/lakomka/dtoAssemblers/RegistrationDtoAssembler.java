package com.lakomka.dtoAssemblers;


import com.lakomka.dto.OrganizationType;
import com.lakomka.dto.RegistrationDto;
import com.lakomka.models.person.JPerson;
import org.springframework.stereotype.Component;

@Component
public class RegistrationDtoAssembler {

    /**
     * Определяет тип организации на основе длины ИНН
     */
    public OrganizationType getOrganizationType(RegistrationDto registrationDto) {
        if (registrationDto.getInn() == null) {
            return OrganizationType.UNKNOWN;
        }

        return switch (registrationDto.getInn().length()) {
            case 10 -> OrganizationType.JURIDICAL;
            case 12 -> OrganizationType.INDIVIDUAL;
            default -> OrganizationType.UNKNOWN;
        };
    }

    public Object toEntity(RegistrationDto registrationDto){
        if (isIndividual(registrationDto)){
            ///todo: сейчас поддержки ФЛ нет
            return null;
//            return new Person(registrationDto);
        } else if (isJuridical(registrationDto)) {
            return new JPerson(registrationDto);
        }
        return null;
    }

    /**
     * Проверяет, является ли организация юридическим лицом
     */
    public boolean isJuridical(RegistrationDto registrationDto) {
        return getOrganizationType(registrationDto) == OrganizationType.JURIDICAL;
    }

    /**
     * Проверяет, является ли организация ИП/физлицом
     */
    public boolean isIndividual(RegistrationDto registrationDto) {
        return getOrganizationType(registrationDto) == OrganizationType.INDIVIDUAL;
    }

}
