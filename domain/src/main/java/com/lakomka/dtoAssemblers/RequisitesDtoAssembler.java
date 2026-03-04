package com.lakomka.dtoAssemblers;


import com.lakomka.dto.CreateJPersonDto;
import com.lakomka.dto.OrganizationType;
import com.lakomka.models.person.JPerson;
import org.springframework.stereotype.Component;

@Component
public class RequisitesDtoAssembler {

    /**
     * Определяет тип организации на основе длины ИНН
     */
    public OrganizationType getOrganizationType(CreateJPersonDto createJPersonDto) {
        if (createJPersonDto.getInn() == null) {
            return OrganizationType.UNKNOWN;
        }

        return switch (createJPersonDto.getInn().length()) {
            case 10 -> OrganizationType.JURIDICAL;
            case 12 -> OrganizationType.INDIVIDUAL;
            default -> OrganizationType.UNKNOWN;
        };
    }

    /**
     * Для превращения дто для создания ЮЛ или ФЛ, ИП в конкретные дто, устарело, можно упростить.
     * <p>
     * todo: упростить
     */
    public Object toEntity(CreateJPersonDto createJPersonDto){
        if (isIndividual(createJPersonDto)){
            ///todo: сейчас поддержки ФЛ нет
            return null;
//            return new Person(registrationDto);
        } else if (isJuridical(createJPersonDto)) {
            return new JPerson(createJPersonDto);
        }
        return null;
    }

    /**
     * Проверяет, является ли организация юридическим лицом
     */
    public boolean isJuridical(CreateJPersonDto createJPersonDto) {
        return getOrganizationType(createJPersonDto) == OrganizationType.JURIDICAL;
    }

    /**
     * Проверяет, является ли организация ИП/физлицом
     */
    public boolean isIndividual(CreateJPersonDto createJPersonDto) {
        return getOrganizationType(createJPersonDto) == OrganizationType.INDIVIDUAL;
    }

}
