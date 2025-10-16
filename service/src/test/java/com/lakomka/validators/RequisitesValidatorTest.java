package com.lakomka.validators;

import com.lakomka.validators.RequisitesValidator.CompanyRequisites;
import com.lakomka.validators.RequisitesValidator.IndividualRequisites;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SuppressWarnings("DataFlowIssue")
class RequisitesValidatorTest {

    private RequisitesValidator validator;

    @BeforeEach
    void setUp() {
        validator = new RequisitesValidator();
    }

    // =============================================
    // Тесты для supports()
    // =============================================

    @Test
    @DisplayName("supports() должен возвращать true для CompanyRequisites и IndividualRequisites")
    void supports_shouldReturnTrueForSupportedClasses() {
        assertTrue(validator.supports(CompanyRequisites.class));
        assertTrue(validator.supports(IndividualRequisites.class));
    }

    @Test
    @DisplayName("supports() должен возвращать false для неподдерживаемых классов")
    void supports_shouldReturnFalseForUnsupportedClasses() {
        assertFalse(validator.supports(String.class));
        assertFalse(validator.supports(Object.class));
        assertFalse(validator.supports(Integer.class));
    }

    // =============================================
    // Тесты для ИНН юридического лица (10 цифр)
    // =============================================

    @Test
    @DisplayName("Валидный ИНН юридического лица должен проходить проверку")
    void validateInnJuridical_withValidInn_shouldNotHaveErrors() {
        // Яндекс
        String validInn = "7725088527";
        Errors errors = new BeanPropertyBindingResult(new CompanyRequisites(), "target");

        validator.validateInnJuridical(validInn, "inn", errors);

        assertFalse(errors.hasErrors(), "Не должно быть ошибок для валидного ИНН");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "7725088528", // Неверное контрольное число
            "1234567890" // Случайный невалидный ИНН
    })
    @DisplayName("Невалидный ИНН юридического лица должен вызывать ошибку")
    void validateInnJuridical_withInvalidInn_shouldHaveErrors(String invalidInn) {
        Errors errors = new BeanPropertyBindingResult(new CompanyRequisites(), "target");

        validator.validateInnJuridical(invalidInn, "inn", errors);

        assertTrue(errors.hasErrors(), "Должна быть ошибка для невалидного ИНН");
        assertEquals("inn.invalid.checksum", errors.getFieldError("inn").getCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123456789",   // 9 цифр
            "12345678901", // 11 цифр
            "abc",         // не цифры
            ""             // пустая строка
    })
    @DisplayName("ИНН юридического лица с неправильной длиной должен вызывать ошибку")
    void validateInnJuridical_withWrongLength_shouldHaveErrors(String wrongLengthInn) {
        Errors errors = new BeanPropertyBindingResult(new CompanyRequisites(), "target");

        validator.validateInnJuridical(wrongLengthInn, "inn", errors);

        assertTrue(errors.hasErrors(), "Должна быть ошибка длины или формата");
    }

    @Test
    @DisplayName("Пустой ИНН юридического лица должен вызывать ошибку")
    void validateInnJuridical_withNullInn_shouldHaveErrors() {
        Errors errors = new BeanPropertyBindingResult(new CompanyRequisites(), "target");

        validator.validateInnJuridical(null, "inn", errors);

        assertTrue(errors.hasErrors());
        assertEquals("inn.empty", errors.getFieldError("inn").getCode());
    }

    @Test
    @DisplayName("ИНН юридического лица с нецифровыми символами должен вызывать ошибку")
    void validateInnJuridical_withNonDigitCharacters_shouldHaveErrors() {
        Errors errors = new BeanPropertyBindingResult(new CompanyRequisites(), "target");

        validator.validateInnJuridical("77A5088527", "inn", errors);

        assertTrue(errors.hasErrors());
        assertEquals("inn.invalid.format", errors.getFieldError("inn").getCode());
    }

    // =============================================
    // Тесты для ИНН физического лица (12 цифр)
    // =============================================

    @Test
    @DisplayName("Валидный ИНН физического лица должен проходить проверку")
    void validateInnIndividual_withValidInn_shouldNotHaveErrors() {
        // Пример валидного ИНН ФЛ
        String validInn = "701718298828";
        Errors errors = new BeanPropertyBindingResult(new IndividualRequisites(), "target");

        validator.validateInnIndividual(validInn, "inn", errors);

        assertFalse(errors.hasErrors(), "Не должно быть ошибок для валидного ИНН ФЛ");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "500100796250", // Неверное второе контрольное число
            "123456789012" // Случайный невалидный ИНН
    })
    @DisplayName("Невалидный ИНН физического лица должен вызывать ошибку")
    void validateInnIndividual_withInvalidInn_shouldHaveErrors(String invalidInn) {
        Errors errors = new BeanPropertyBindingResult(new IndividualRequisites(), "target");

        validator.validateInnIndividual(invalidInn, "inn", errors);

        assertTrue(errors.hasErrors(), "Должна быть ошибка для невалидного ИНН ФЛ");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678901",  // 11 цифр
            "1234567890123", // 13 цифр
            "abcdefghijkl", // не цифры
            ""              // пустая строка
    })
    @DisplayName("ИНН физического лица с неправильной длиной должен вызывать ошибку")
    void validateInnIndividual_withWrongLength_shouldHaveErrors(String wrongLengthInn) {
        Errors errors = new BeanPropertyBindingResult(new CompanyRequisites(), "target");

        validator.validateInnIndividual(wrongLengthInn, "inn", errors);

        assertTrue(errors.hasErrors(), "Должна быть ошибка длины или формата");
    }

    // =============================================
    // Тесты для ОГРН (13 цифр)
    // =============================================

    @Test
    @DisplayName("Валидный ОГРН должен проходить проверку")
    void validateOgrn_withValidOgrn_shouldNotHaveErrors() {
        // Яндекс
        String validOgrn = "1027700229193";
        Errors errors = new BeanPropertyBindingResult(new CompanyRequisites(), "target");

        validator.validateOgrn(validOgrn, "ogrn", errors);

        assertFalse(errors.hasErrors(), "Не должно быть ошибок для валидного ОГРН");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1027700229194", // Неверное контрольное число
            "1234567890123", // Случайный невалидный ОГРН
    })
    @DisplayName("Невалидный ОГРН должен вызывать ошибку")
    void validateOgrn_withInvalidOgrn_shouldHaveErrors(String invalidOgrn) {
        Errors errors = new BeanPropertyBindingResult(new CompanyRequisites(), "target");

        validator.validateOgrn(invalidOgrn, "ogrn", errors);

        assertTrue(errors.hasErrors(), "Должна быть ошибка для невалидного ОГРН");
        assertEquals("ogrn.invalid.checksum", errors.getFieldError("ogrn").getCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123456789012",  // 12 цифр
            "12345678901234", // 14 цифр
            "abcdefghijklm", // не цифры
            ""               // пустая строка
    })
    @DisplayName("ОГРН с неправильной длиной должен вызывать ошибку")
    void validateOgrn_withWrongLength_shouldHaveErrors(String wrongLengthOgrn) {
        Errors errors = new BeanPropertyBindingResult(new CompanyRequisites(), "target");

        validator.validateOgrn(wrongLengthOgrn, "ogrn", errors);

        assertTrue(errors.hasErrors(), "Должна быть ошибка длины или формата");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "3027700229193", // Первая цифра 3 (только для ОГРНИП)
            "4027700229193", // Первая цифра 4
            "9027700229193"  // Первая цифра 9
    })
    @DisplayName("ОГРН с невалидной первой цифрой должен вызывать ошибку")
    void validateOgrn_withInvalidFirstDigit_shouldHaveErrors(String invalidFirstDigitOgrn) {
        Errors errors = new BeanPropertyBindingResult(new CompanyRequisites(), "target");

        validator.validateOgrn(invalidFirstDigitOgrn, "ogrn", errors);

        assertTrue(errors.hasErrors(), "Должна быть ошибка для невалидной первой цифры");
        //assertEquals("ogrn.invalid.first.digit", errors.getFieldError("ogrn").getCode());
        assertEquals("ogrn.invalid.checksum", errors.getFieldError("ogrn").getCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1027700229193", // Первая цифра 1
    })
    @DisplayName("ОГРН с валидной первой цифрой должен проходить проверку")
    void validateOgrn_withValidFirstDigit_shouldNotHaveErrors(String validFirstDigitOgrn) {
        Errors errors = new BeanPropertyBindingResult(new CompanyRequisites(), "target");

        validator.validateOgrn(validFirstDigitOgrn, "ogrn", errors);

        assertFalse(errors.hasErrors(), "Не должно быть ошибок для валидной первой цифры");
    }

    // =============================================
    // Тесты для ОГРНИП (15 цифр)
    // =============================================

    @Test
    @DisplayName("Валидный ОГРНИП должен проходить проверку")
    void validateOgrnip_withValidOgrnip_shouldNotHaveErrors() {
        // Пример валидного ОГРНИП
        String validOgrnip = "304500116000157";
        Errors errors = new BeanPropertyBindingResult(new CompanyRequisites(), "target");

        validator.validateOgrnip(validOgrnip, "ogrnip", errors);

        assertFalse(errors.hasErrors(), "Не должно быть ошибок для валидного ОГРНИП");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "304500116000158", // Неверное контрольное число
            "123456789012345" // Случайный невалидный ОГРНИП
    })
    @DisplayName("Невалидный ОГРНИП должен вызывать ошибку")
    void validateOgrnip_withInvalidOgrnip_shouldHaveErrors(String invalidOgrnip) {
        Errors errors = new BeanPropertyBindingResult(new IndividualRequisites(), "target");

        validator.validateOgrnip(invalidOgrnip, "ogrnip", errors);

        assertTrue(errors.hasErrors(), "Должна быть ошибка для невалидного ОГРНИП");
        assertEquals("ogrnip.invalid.checksum", errors.getFieldError("ogrnip").getCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678901234",  // 14 цифр
            "1234567890123456", // 16 цифр
            "abcdefghijklmno", // не цифры
            ""                 // пустая строка
    })
    @DisplayName("ОГРНИП с неправильной длиной должен вызывать ошибку")
    void validateOgrnip_withWrongLength_shouldHaveErrors(String wrongLengthOgrnip) {
        Errors errors = new BeanPropertyBindingResult(new IndividualRequisites(), "target");

        validator.validateOgrnip(wrongLengthOgrnip, "ogrnip", errors);

        assertTrue(errors.hasErrors(), "Должна быть ошибка длины или формата");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "104500116000157", // Первая цифра 1
            "204500116000157", // Первая цифра 2
            "504500116000157"  // Первая цифра 5
    })
    @DisplayName("ОГРНИП с первой цифрой не равной 3 должен вызывать ошибку")
    void validateOgrnip_withInvalidFirstDigit_shouldHaveErrors(String invalidFirstDigitOgrnip) {
        Errors errors = new BeanPropertyBindingResult(new IndividualRequisites(), "target");

        validator.validateOgrnip(invalidFirstDigitOgrnip, "ogrnip", errors);

        assertTrue(errors.hasErrors(), "Должна быть ошибка для невалидной первой цифры");
        //assertEquals("ogrnip.invalid.first.digit", errors.getFieldError("ogrnip").getCode());
        assertEquals("ogrnip.invalid.checksum", errors.getFieldError("ogrnip").getCode());
    }

    // =============================================
    // Тесты для КПП (9 символов)
    // =============================================

    @Test
    @DisplayName("Валидный КПП должен проходить проверку")
    void validateKpp_withValidKpp_shouldNotHaveErrors() {
        // Яндекс
        String validKpp = "770501001";
        Errors errors = new BeanPropertyBindingResult(new CompanyRequisites(), "target");

        validator.validateKpp(validKpp, "kpp", errors);

        assertFalse(errors.hasErrors(), "Не должно быть ошибок для валидного КПП");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "770501001", // Яндекс
            "773601001", // Сбербанк
            "783901001"  // Газпром
    })
    @DisplayName("Разные валидные КПП должны проходить проверку")
    void validateKpp_withVariousValidKpp_shouldNotHaveErrors(String validKpp) {
        Errors errors = new BeanPropertyBindingResult(new CompanyRequisites(), "target");

        validator.validateKpp(validKpp, "kpp", errors);

        assertFalse(errors.hasErrors(), "Не должно быть ошибок для валидного КПП: " + validKpp);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123",           // слишком короткий
            "1234567890",    // слишком длинный
            "77A501001",     // недопустимые символы
            "77050100",      // 8 символов
            "7705010012"     // 10 символов
    })
    @DisplayName("КПП с неправильной длиной или форматом должен вызывать ошибку")
    void validateKpp_withWrongLengthOrFormat_shouldHaveErrors(String invalidKpp) {
        Errors errors = new BeanPropertyBindingResult(new CompanyRequisites(), "target");

        validator.validateKpp(invalidKpp, "kpp", errors);

        assertTrue(errors.hasErrors(), "Должна быть ошибка для невалидного КПП: " + invalidKpp);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "770599001", // Несуществующий код причины
            "770500001", // Невалидный код причины
            "770599999"  // Невалидный код причины
    })
    @DisplayName("КПП с невалидным кодом причины должен вызывать ошибку")
    void validateKpp_withInvalidReasonCode_shouldHaveErrors(String invalidReasonKpp) {
        Errors errors = new BeanPropertyBindingResult(new CompanyRequisites(), "target");

        validator.validateKpp(invalidReasonKpp, "kpp", errors);

        assertTrue(errors.hasErrors(), "Должна быть ошибка для невалидного кода причины");
        assertEquals("kpp.invalid.reason", errors.getFieldError("kpp").getCode());
    }

    @Test
    @DisplayName("Пустой КПП должен вызывать ошибку")
    void validateKpp_withNullKpp_shouldHaveErrors() {
        Errors errors = new BeanPropertyBindingResult(new CompanyRequisites(), "target");

        validator.validateKpp(null, "kpp", errors);

        assertTrue(errors.hasErrors());
        assertEquals("kpp.empty", errors.getFieldError("kpp").getCode());
    }

    // =============================================
    // Интеграционные тесты для полных объектов
    // =============================================

    @Test
    @DisplayName("Валидация полного объекта CompanyRequisites с валидными данными")
    void validate_withValidCompanyRequisites_shouldNotHaveErrors() {
        CompanyRequisites requisites = new CompanyRequisites(
                "7725088527",     // Яндекс ИНН
                "1027700229193",  // Яндекс ОГРН
                "770501001"       // Яндекс КПП
        );

        Errors errors = new BeanPropertyBindingResult(requisites, "companyRequisites");

        validator.validate(requisites, errors);

        assertFalse(errors.hasErrors(), "Не должно быть ошибок для валидных реквизитов компании");
    }

    @Test
    @DisplayName("Валидация полного объекта IndividualRequisites с валидными данными")
    void validate_withValidIndividualRequisites_shouldNotHaveErrors() {
        IndividualRequisites requisites = new IndividualRequisites(
                "701718298828",    // Пример ИНН ФЛ
                "312701727000111"  // Пример ОГРНИП
        );

        Errors errors = new BeanPropertyBindingResult(requisites, "individualRequisites");

        validator.validate(requisites, errors);

        assertFalse(errors.hasErrors(), "Не должно быть ошибок для валидных реквизитов ИП");
    }

    @Test
    @DisplayName("Валидация CompanyRequisites с невалидными данными должна возвращать ошибки")
    void validate_withInvalidCompanyRequisites_shouldHaveErrors() {
        CompanyRequisites requisites = new CompanyRequisites(
                "7725088528",     // Невалидный ИНН
                "1027700229194",  // Невалидный ОГРН
                "770599001"       // Невалидный КПП
        );

        Errors errors = new BeanPropertyBindingResult(requisites, "companyRequisites");

        validator.validate(requisites, errors);

        assertTrue(errors.hasErrors(), "Должны быть ошибки для невалидных реквизитов");
        assertEquals(3, errors.getErrorCount(), "Должно быть 3 ошибки");
    }

    @Test
    @DisplayName("Валидация IndividualRequisites с невалидными данными должна возвращать ошибки")
    void validate_withInvalidIndividualRequisites_shouldHaveErrors() {
        IndividualRequisites requisites = new IndividualRequisites(
                "500100796250",    // Невалидный ИНН
                "104500116000157"  // Невалидный ОГРНИП (первая цифра 1 вместо 3)
        );

        Errors errors = new BeanPropertyBindingResult(requisites, "individualRequisites");

        validator.validate(requisites, errors);

        assertTrue(errors.hasErrors(), "Должны быть ошибки для невалидных реквизитов");
        assertEquals(3, errors.getErrorCount(), "Должно быть 3 ошибки");
    }

    @Test
    @DisplayName("Валидация объекта неподдерживаемого типа не должна вызывать исключений")
    void validate_withUnsupportedObjectType_shouldNotThrowException() {
        String unsupportedObject = "test";
        Errors errors = new BeanPropertyBindingResult(unsupportedObject, "unsupported");

        // Не должно бросать исключение
        assertDoesNotThrow(() -> validator.validate(unsupportedObject, errors));

        // И не должно добавлять ошибки (но и не валидировать)
        assertFalse(errors.hasErrors());
    }

    // =============================================
    // Параметризованные тесты с реальными данными
    // =============================================

    @ParameterizedTest
    @CsvSource({
            // Компания, ИНН, ОГРН, КПП
            "Яндекс,          7725088527, 1027700229193, 770501001",
            "Сбербанк,        7707083893, 1027700132195, 773601001",
            "Газпром,         7736050003, 1027700070518, 783901001",
            "ВТБ,             7702070139, 1027739609391, 784201001",
            "РЖД,             7708503727, 1037739877295, 770901001"
    })
    @DisplayName("Тестирование реальных компаний с валидными реквизитами")
    void validate_realCompaniesWithValidRequisites_shouldNotHaveErrors(
            String companyName, String inn, String ogrn, String kpp) {

        CompanyRequisites requisites = new CompanyRequisites(inn, ogrn, kpp);
        Errors errors = new BeanPropertyBindingResult(requisites, "companyRequisites");

        validator.validate(requisites, errors);

        assertFalse(errors.hasErrors(),
                String.format("Не должно быть ошибок для %s. Ошибки: %s",
                        companyName, errors.getAllErrors()));
    }

    @ParameterizedTest
    @CsvSource({
            // ИНН, Ожидаемый результат (true - валидный, false - невалидный)
            "7725088527, true",
            "7725088528, false",
            "423006310840, true",
            "500100796250, false",
            "1234567890, false",
            "0000000000, true",
            "0000000001, false"
    })
    @DisplayName("Параметризованная проверка ИНН разных типов")
    void validateInn_parameterizedTest(String inn, boolean expectedValid) {
        Errors errors = new BeanPropertyBindingResult(new CompanyRequisites(), "target");

        if (inn.length() == 10) {
            validator.validateInnJuridical(inn, "inn", errors);
        } else {
            validator.validateInnIndividual(inn, "inn", errors);
        }

        if (expectedValid) {
            assertFalse(errors.hasErrors(),
                    String.format("ИНН %s должен быть валидным", inn));
        } else {
            assertTrue(errors.hasErrors(),
                    String.format("ИНН %s должен быть невалидным", inn));
        }
    }

}