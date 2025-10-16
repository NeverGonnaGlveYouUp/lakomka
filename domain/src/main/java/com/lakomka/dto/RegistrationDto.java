package com.lakomka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDto {
    private String inn;
    private String kpp;
    private String ogrn;
    private String deliveryAddress;
    private String jurAddress;
    private String name;
    private String nameFull;
    private String contact;
    private String phone;

    /**
     * Определяет тип организации на основе длины ИНН
     */
    public OrganizationType getOrganizationType() {
        if (inn == null) {
            return OrganizationType.UNKNOWN;
        }

        return switch (inn.length()) {
            case 10 -> OrganizationType.JURIDICAL;
            case 12 -> OrganizationType.INDIVIDUAL;
            default -> OrganizationType.UNKNOWN;
        };
    }

    /**
     * Проверяет, является ли организация юридическим лицом
     */
    public boolean isJuridical() {
        return getOrganizationType() == OrganizationType.JURIDICAL;
    }

    /**
     * Проверяет, является ли организация ИП/физлицом
     */
    public boolean isIndividual() {
        return getOrganizationType() == OrganizationType.INDIVIDUAL;
    }

    public enum OrganizationType {
        JURIDICAL,      // Юридическое лицо (ИНН 10 цифр)
        INDIVIDUAL,     // ИП/Физлицо (ИНН 12 цифр)
        UNKNOWN         // Неизвестный тип
    }
}
