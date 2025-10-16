package com.lakomka.validators;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

@Slf4j
@Component("validatorInnOgrnKppRequisites")
public class RequisitesValidator implements Validator {

    private static final Pattern DIGITS_ONLY = Pattern.compile("^\\d+$");
    private static final Pattern KPP_PATTERN = Pattern.compile("^\\d{4}[0-9A-Z]{2}\\d{3}$");

    // Коды причин постановки на учет для КПП
    private static final String[] VALID_KPP_REASONS = {
            "01", "02", "03", "04", "05", "06", "07", "08", "10", "11", "12",
            "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23",
            "24", "25", "26", "27", "28", "29", "30", "31", "32", "35"
    };

    @Override
    public boolean supports(Class<?> clazz) {
        return CompanyRequisites.class.equals(clazz) ||
                IndividualRequisites.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target instanceof CompanyRequisites) {
            validateCompanyRequisites((CompanyRequisites) target, errors);
        } else if (target instanceof IndividualRequisites) {
            validateIndividualRequisites((IndividualRequisites) target, errors);
        }
    }

    private void validateCompanyRequisites(CompanyRequisites requisites, Errors errors) {
        if (requisites.getInn() != null) {
            validateInnJuridical(requisites.getInn(), "inn", errors);
        }

        if (requisites.getOgrn() != null) {
            validateOgrn(requisites.getOgrn(), "ogrn", errors);
        }

        if (requisites.getKpp() != null) {
            validateKpp(requisites.getKpp(), "kpp", errors);
        }
    }

    private void validateIndividualRequisites(IndividualRequisites requisites, Errors errors) {
        if (requisites.getInn() != null) {
            validateInnIndividual(requisites.getInn(), "inn", errors);
        }

        if (requisites.getOgrnip() != null) {
            validateOgrnip(requisites.getOgrnip(), "ogrnip", errors);
        }
    }

    /**
     * Валидация ИНН юридического лица (10 цифр)
     */
    public void validateInnJuridical(String inn, String field, Errors errors) {
        if (inn == null || inn.trim().isEmpty()) {
            errors.rejectValue(field, "inn.empty", "ИНН не может быть пустым");
            return;
        }

        if (!DIGITS_ONLY.matcher(inn).matches()) {
            errors.rejectValue(field, "inn.invalid.format", "ИНН должен содержать только цифры");
            return;
        }

        if (inn.length() != 10) {
            errors.rejectValue(field, "inn.invalid.length", "ИНН юридического лица должен содержать 10 цифр");
            return;
        }

        // Проверка контрольного числа
        int[] coefficients = {2, 4, 10, 3, 5, 9, 4, 6, 8};
        int sum = 0;

        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(inn.charAt(i)) * coefficients[i];
        }

        int controlNumber = sum % 11;
        if (controlNumber == 10) {
            controlNumber = 0;
        }

        int actualControlNumber = Character.getNumericValue(inn.charAt(9));

        if (controlNumber != actualControlNumber) {
            errors.rejectValue(field, "inn.invalid.checksum", "Неверное контрольное число ИНН");
        }
    }

    /**
     * Валидация ИНН физического лица/ИП (12 цифр)
     */
    public void validateInnIndividual(String inn, String field, Errors errors) {

        if (inn == null || inn.trim().isEmpty()) {
            errors.rejectValue(field, "inn.empty", "ИНН не может быть пустым");
            return;
        }

        if (!DIGITS_ONLY.matcher(inn).matches()) {
            errors.rejectValue(field, "inn.invalid.format", "ИНН должен содержать только цифры");
            return;
        }

        if (inn.length() != 12) {
            errors.rejectValue(field, "inn.invalid.length", "ИНН физического лица должен содержать 12 цифр");
            return;
        }

        // Проверка первого контрольного числа (11-я цифра)
        int[] coefficients1 = {7, 2, 4, 10, 3, 5, 9, 4, 6, 8};
        int sum1 = 0;

        for (int i = 0; i < 10; i++) {
            sum1 += Character.getNumericValue(inn.charAt(i)) * coefficients1[i];
        }

        int controlNumber1 = sum1 % 11;
        if (controlNumber1 == 10) {
            controlNumber1 = 0;
        }

        int actualControlNumber1 = Character.getNumericValue(inn.charAt(10));

        if (controlNumber1 != actualControlNumber1) {
            errors.rejectValue(field, "inn.invalid.checksum.first", "Неверное первое контрольное число ИНН");
            return;
        }

        // Проверка второго контрольного числа (12-я цифра)
        int[] coefficients2 = {3, 7, 2, 4, 10, 3, 5, 9, 4, 6, 8};
        int sum2 = 0;

        for (int i = 0; i < 11; i++) {
            sum2 += Character.getNumericValue(inn.charAt(i)) * coefficients2[i];
        }

        int controlNumber2 = sum2 % 11;
        if (controlNumber2 == 10) {
            controlNumber2 = 0;
        }

        int actualControlNumber2 = Character.getNumericValue(inn.charAt(11));

        if (controlNumber2 != actualControlNumber2) {
            errors.rejectValue(field, "inn.invalid.checksum.second", "Неверное второе контрольное число ИНН");
        }
    }

    /**
     * Валидация ОГРН (13 цифр)
     */
    public void validateOgrn(String ogrn, String field, Errors errors) {
        if (ogrn == null || ogrn.trim().isEmpty()) {
            errors.rejectValue(field, "ogrn.empty", "ОГРН не может быть пустым");
            return;
        }

        if (!DIGITS_ONLY.matcher(ogrn).matches()) {
            errors.rejectValue(field, "ogrn.invalid.format", "ОГРН должен содержать только цифры");
            return;
        }

        if (ogrn.length() != 13) {
            errors.rejectValue(field, "ogrn.invalid.length", "ОГРН должен содержать 13 цифр");
            return;
        }

        // Проверка контрольного числа
        long number = Long.parseLong(ogrn.substring(0, 12));
        int controlNumber = (int) (number % 11);
        if (controlNumber == 10) {
            controlNumber = 0;
        }

        int actualControlNumber = Character.getNumericValue(ogrn.charAt(12));

        if (controlNumber != actualControlNumber) {
            errors.rejectValue(field, "ogrn.invalid.checksum", "Неверное контрольное число ОГРН");
        }

        // Дополнительная проверка: первый символ должен быть 1, 2 или 5
        char firstChar = ogrn.charAt(0);
        if (firstChar != '1' && firstChar != '2' && firstChar != '5') {
            errors.rejectValue(field, "ogrn.invalid.first.digit", "Первая цифра ОГРН должна быть 1, 2 или 5");
        }
    }

    /**
     * Валидация ОГРНИП (15 цифр)
     */
    public void validateOgrnip(String ogrnip, String field, Errors errors) {
        if (ogrnip == null || ogrnip.trim().isEmpty()) {
            errors.rejectValue(field, "ogrnip.empty", "ОГРНИП не может быть пустым");
            return;
        }

        if (!DIGITS_ONLY.matcher(ogrnip).matches()) {
            errors.rejectValue(field, "ogrnip.invalid.format", "ОГРНИП должен содержать только цифры");
            return;
        }

        if (ogrnip.length() != 15) {
            errors.rejectValue(field, "ogrnip.invalid.length", "ОГРНИП должен содержать 15 цифр");
            return;
        }

        // Проверка контрольного числа
        long number = Long.parseLong(ogrnip.substring(0, 14));
        int controlNumber = (int) (number % 13);
        controlNumber %= 10; // Берем последнюю цифру

        int actualControlNumber = Character.getNumericValue(ogrnip.charAt(14));

        if (controlNumber != actualControlNumber) {
            errors.rejectValue(field, "ogrnip.invalid.checksum", "Неверное контрольное число ОГРНИП");
        }

        // Дополнительная проверка: первый символ должен быть 3
        char firstChar = ogrnip.charAt(0);
        if (firstChar != '3') {
            errors.rejectValue(field, "ogrnip.invalid.first.digit", "Первая цифра ОГРНИП должна быть 3");
        }
    }

    /**
     * Валидация КПП (9 символов)
     */
    public void validateKpp(String kpp, String field, Errors errors) {
        if (kpp == null || kpp.trim().isEmpty()) {
            errors.rejectValue(field, "kpp.empty", "КПП не может быть пустым");
            return;
        }

        if (kpp.length() != 9) {
            errors.rejectValue(field, "kpp.invalid.length", "КПП должен содержать 9 символов");
            return;
        }

        if (!KPP_PATTERN.matcher(kpp).matches()) {
            errors.rejectValue(field, "kpp.invalid.format",
                    "КПП должен иметь формат: 4 цифры, 2 символа (цифры или заглавные буквы), 3 цифры");
            return;
        }

        // Проверка кода причины постановки (5-й и 6-й символы)
        String reasonCode = kpp.substring(4, 6);
        boolean validReason = false;
        for (String validCode : VALID_KPP_REASONS) {
            if (validCode.equals(reasonCode)) {
                validReason = true;
                break;
            }
        }

        if (!validReason) {
            errors.rejectValue(field, "kpp.invalid.reason", "Неверный код причины постановки на учет");
        }
    }

    // Статические методы для создания типизированных валидаторов
    public static Validator forCompanyRequisites(BiConsumer<CompanyRequisites, Errors> delegate) {
        Objects.requireNonNull(delegate);
        return Validator.forType(CompanyRequisites.class, delegate);
    }

    public static Validator forIndividualRequisites(BiConsumer<IndividualRequisites, Errors> delegate) {
        Objects.requireNonNull(delegate);
        return Validator.forType(IndividualRequisites.class, delegate);
    }

    // Вспомогательный класс для типизированной валидации
    private static class TypedValidator<T> implements Validator {
        private final Class<T> targetClass;
        private final java.util.function.Predicate<Class<?>> supportsPredicate;
        private final BiConsumer<T, Errors> delegate;

        public TypedValidator(Class<T> targetClass,
                              java.util.function.Predicate<Class<?>> supportsPredicate,
                              BiConsumer<T, Errors> delegate) {
            this.targetClass = targetClass;
            this.supportsPredicate = supportsPredicate;
            this.delegate = delegate;
        }

        @Override
        public boolean supports(Class<?> clazz) {
            return supportsPredicate.test(clazz);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void validate(Object target, Errors errors) {
            delegate.accept((T) target, errors);
        }
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IndividualRequisites {
        // Getters and Setters
        private String inn; // ИНН физического лица/ИП (12 цифр)
        private String ogrnip; // ОГРНИП (15 цифр)
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CompanyRequisites {
        private String inn; // ИНН юридического лица (10 цифр)
        private String ogrn; // ОГРН (13 цифр)
        private String kpp; // КПП (9 символов)
    }
}
