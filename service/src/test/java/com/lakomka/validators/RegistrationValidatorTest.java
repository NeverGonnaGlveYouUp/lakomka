package com.lakomka.validators;

import com.lakomka.dto.RegistrationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
class RegistrationValidatorTest {

    @Mock
    private RequisitesValidator requisitesValidator;

    private RegistrationValidator validator;

    @BeforeEach
    void setUp() {
        validator = new RegistrationValidator(requisitesValidator);
    }

    // =============================================
    // Тесты для supports()
    // =============================================

    @Test
    @DisplayName("supports() должен возвращать true для RegistrationDto")
    void supports_shouldReturnTrueForRegistrationDto() {
        assertTrue(validator.supports(RegistrationDto.class));
    }

    @Test
    @DisplayName("supports() должен возвращать false для других классов")
    void supports_shouldReturnFalseForOtherClasses() {
        assertFalse(validator.supports(String.class));
        assertFalse(validator.supports(Object.class));
    }

    // =============================================
    // Тесты для юридических лиц (ИНН 10 цифр)
    // =============================================

    @Test
    @DisplayName("Валидный DTO юридического лица должен проходить проверку")
    void validate_withValidJuridicalDto_shouldNotHaveErrors() {
        // Arrange
        RegistrationDto dto = createValidJuridicalDto();
        Errors errors = new BeanPropertyBindingResult(dto, "RegistrationDto");

        // Мокируем вызовы валидатора реквизитов - все методы не должны добавлять ошибки
        doNothing().when(requisitesValidator).validateInnJuridical(anyString(), anyString(), any(Errors.class));
        doNothing().when(requisitesValidator).validateOgrn(anyString(), anyString(), any(Errors.class));
        doNothing().when(requisitesValidator).validateKpp(anyString(), anyString(), any(Errors.class));

        // Act
        validator.validate(dto, errors);

        // Assert
        assertFalse(errors.hasErrors(), "Не должно быть ошибок для валидного DTO юрлица");
        verify(requisitesValidator).validateInnJuridical(eq("7725088527"), eq("inn"), eq(errors));
        verify(requisitesValidator).validateOgrn(eq("1027700229193"), eq("ogrn"), eq(errors));
        verify(requisitesValidator).validateKpp(eq("770501001"), eq("kpp"), eq(errors));
    }

    @Test
    @DisplayName("DTO юридического лица с ошибками в реквизитах должен возвращать ошибки")
    void validate_withInvalidJuridicalRequisites_shouldHaveErrors() {
        // Arrange
        RegistrationDto dto = createValidJuridicalDto();
        Errors errors = new BeanPropertyBindingResult(dto, "RegistrationDto");

        // Мокируем вызовы валидатора реквизитов - добавляем ошибки
        doAnswer(invocation -> {
            Errors errorsArg = invocation.getArgument(2);
            errorsArg.rejectValue("inn", "inn.invalid");
            return null;
        }).when(requisitesValidator).validateInnJuridical(anyString(), anyString(), any(Errors.class));

        doAnswer(invocation -> {
            Errors errorsArg = invocation.getArgument(2);
            errorsArg.rejectValue("ogrn", "ogrn.invalid");
            return null;
        }).when(requisitesValidator).validateOgrn(anyString(), anyString(), any(Errors.class));

        // Act
        validator.validate(dto, errors);

        // Assert
        assertTrue(errors.hasErrors(), "Должны быть ошибки при невалидных реквизитах");
        assertNotNull(errors.getFieldError("inn"));
        assertNotNull(errors.getFieldError("ogrn"));
    }

    @Test
    @DisplayName("DTO юридического лица без КПП должен вызывать ошибку")
    void validate_juridicalDtoWithoutKpp_shouldHaveError() {
        // Arrange
        RegistrationDto dto = createValidJuridicalDto();
        dto.setKpp(null); // Убираем КПП
        Errors errors = new BeanPropertyBindingResult(dto, "RegistrationDto");

        // Act
        validator.validate(dto, errors);

        // Assert
        assertTrue(errors.hasErrors(), "Должна быть ошибка об отсутствии КПП");
        assertNotNull(errors.getFieldError("kpp"));
        assertEquals("kpp.required", errors.getFieldError("kpp").getCode());
    }

    @Test
    @DisplayName("DTO юридического лица без юридического адреса должен вызывать ошибку")
    void validate_juridicalDtoWithoutJurAddress_shouldHaveError() {
        // Arrange
        RegistrationDto dto = createValidJuridicalDto();
        dto.setJurAddress(null); // Убираем юрадрес
        Errors errors = new BeanPropertyBindingResult(dto, "RegistrationDto");

        // Мокируем успешную валидацию реквизитов
        doNothing().when(requisitesValidator).validateInnJuridical(anyString(), anyString(), any(Errors.class));
        doNothing().when(requisitesValidator).validateOgrn(anyString(), anyString(), any(Errors.class));
        doNothing().when(requisitesValidator).validateKpp(anyString(), anyString(), any(Errors.class));

        // Act
        validator.validate(dto, errors);

        // Assert
        assertTrue(errors.hasErrors(), "Должна быть ошибка об отсутствии юрадреса");
        assertNotNull(errors.getFieldError("jurAddress"));
        assertEquals("jurAddress.required", errors.getFieldError("jurAddress").getCode());
    }

    // =============================================
    // Тесты для ИП/физлиц (ИНН 12 цифр)
    // =============================================

    @Test
    @DisplayName("Валидный DTO ИП должен проходить проверку")
    void validate_withValidIndividualDto_shouldNotHaveErrors() {
        // Arrange
        RegistrationDto dto = createValidIndividualDto();
        Errors errors = new BeanPropertyBindingResult(dto, "RegistrationDto");

        // Мокируем вызовы валидатора реквизитов
        doNothing().when(requisitesValidator).validateInnIndividual(anyString(), anyString(), any(Errors.class));
        doNothing().when(requisitesValidator).validateOgrnip(anyString(), anyString(), any(Errors.class));

        // Act
        validator.validate(dto, errors);

        // Assert
        assertFalse(errors.hasErrors(), "Не должно быть ошибок для валидного DTO ИП");
        verify(requisitesValidator).validateInnIndividual(eq("500100796259"), eq("inn"), eq(errors));
        verify(requisitesValidator).validateOgrnip(eq("304500116000157"), eq("ogrn"), eq(errors));
        verify(requisitesValidator, never()).validateKpp(anyString(), anyString(), any(Errors.class));
    }

    @Test
    @DisplayName("DTO ИП с указанным КПП должен вызывать ошибку")
    void validate_individualDtoWithKpp_shouldHaveError() {
        // Arrange
        RegistrationDto dto = createValidIndividualDto();
        dto.setKpp("770501001"); // Указываем КПП для ИП - это ошибка
        Errors errors = new BeanPropertyBindingResult(dto, "RegistrationDto");

        // Мокируем успешную валидацию остальных реквизитов
        doNothing().when(requisitesValidator).validateInnIndividual(anyString(), anyString(), any(Errors.class));
        doNothing().when(requisitesValidator).validateOgrnip(anyString(), anyString(), any(Errors.class));

        // Act
        validator.validate(dto, errors);

        // Assert
        assertTrue(errors.hasErrors(), "Должна быть ошибка о наличии КПП у ИП");
        assertNotNull(errors.getFieldError("kpp"));
        assertEquals("kpp.invalid.individual", errors.getFieldError("kpp").getCode());
    }

    @Test
    @DisplayName("DTO ИП с ОГРН не 15 цифр должен вызывать ошибку")
    void validate_individualDtoWithWrongOgrnLength_shouldHaveError() {
        // Arrange
        RegistrationDto dto = createValidIndividualDto();
        dto.setOgrn("1027700229193"); // ОГРН 13 цифр вместо ОГРНИП 15 цифр
        Errors errors = new BeanPropertyBindingResult(dto, "RegistrationDto");

        // Мокируем успешную валидацию ИНН, но не мокируем ОГРНИП (будет вызван реальный код)
        doNothing().when(requisitesValidator).validateInnIndividual(anyString(), anyString(), any(Errors.class));

        // Act
        validator.validate(dto, errors);

        // Assert
        assertTrue(errors.hasErrors(), "Должна быть ошибка о неверной длине ОГРНИП");
        assertNotNull(errors.getFieldError("ogrn"));
        assertEquals("ogrnip.invalid.length.individual", errors.getFieldError("ogrn").getCode());
    }

    // =============================================
    // Тесты для обязательных полей
    // =============================================

    @Test
    @DisplayName("DTO без ИНН должен вызывать ошибку")
    void validate_dtoWithoutInn_shouldHaveError() {
        // Arrange
        RegistrationDto dto = createValidJuridicalDto();
        dto.setInn(null);
        Errors errors = new BeanPropertyBindingResult(dto, "RegistrationDto");

        // Act
        validator.validate(dto, errors);

        // Assert
        assertTrue(errors.hasErrors(), "Должна быть ошибка об отсутствии ИНН");
        assertNotNull(errors.getFieldError("inn"));
        assertEquals("inn.required", errors.getFieldError("inn").getCode());

        // Проверяем, что валидатор реквизитов не вызывался
        verifyNoInteractions(requisitesValidator);
    }

    @Test
    @DisplayName("DTO без названия должен вызывать ошибку")
    void validate_dtoWithoutName_shouldHaveError() {
        // Arrange
        RegistrationDto dto = createValidJuridicalDto();
        dto.setName(null);
        Errors errors = new BeanPropertyBindingResult(dto, "RegistrationDto");

        // Act
        validator.validate(dto, errors);

        // Assert
        assertTrue(errors.hasErrors(), "Должна быть ошибка об отсутствии названия");
        assertNotNull(errors.getFieldError("name"));
        assertEquals("name.required", errors.getFieldError("name").getCode());
    }

    @Test
    @DisplayName("DTO без ОГРН должен вызывать ошибку")
    void validate_dtoWithoutOgrn_shouldHaveError() {
        // Arrange
        RegistrationDto dto = createValidJuridicalDto();
        dto.setOgrn(null);
        Errors errors = new BeanPropertyBindingResult(dto, "RegistrationDto");

        // Act
        validator.validate(dto, errors);

        // Assert
        assertTrue(errors.hasErrors(), "Должна быть ошибка об отсутствии ОГРН");
        assertNotNull(errors.getFieldError("ogrn"));
        assertEquals("ogrn.required", errors.getFieldError("ogrn").getCode());
    }

    // =============================================
    // Тесты для невалидного типа организации
    // =============================================

    @ParameterizedTest
    @ValueSource(strings = {
            "123456789",   // 9 цифр
            "12345678901", // 11 цифр  
            "1234567890123", // 13 цифр
            "abc"         // не цифры
    })
    @DisplayName("DTO с ИНН неправильной длины должен вызывать ошибку")
    void validate_dtoWithInvalidInnLength_shouldHaveError(String invalidInn) {
        // Arrange
        RegistrationDto dto = createValidJuridicalDto();
        dto.setInn(invalidInn);
        Errors errors = new BeanPropertyBindingResult(dto, "RegistrationDto");

        lenient().doCallRealMethod().when(requisitesValidator).validateInnIndividual(anyString(), anyString(), any());

        // Act
        validator.validate(dto, errors);

        // Assert
        assertTrue(errors.hasErrors(), "Должна быть ошибка о неверной длине ИНН");
        assertNotNull(errors.getFieldError("inn"));
        assertEquals("inn.invalid.length", errors.getFieldError("inn").getCode());


        // Проверяем, что валидатор реквизитов не вызывался
        verifyNoInteractions(requisitesValidator);
    }

    // =============================================
    // Тесты для проверки телефона
    // =============================================

    @ParameterizedTest
    @ValueSource(strings = {
            "+7(495)123-45-67",
            "84951234567",
            "8-495-123-45-67",
            "+1-800-123-4567",
            "12345"
    })
    @DisplayName("Валидные форматы телефона должны проходить проверку")
    void validate_withValidPhoneFormats_shouldNotHaveErrors(String validPhone) {
        // Arrange
        RegistrationDto dto = createValidJuridicalDto();
        dto.setPhone(validPhone);
        Errors errors = new BeanPropertyBindingResult(dto, "RegistrationDto");

        // Мокируем успешную валидацию реквизитов
        doNothing().when(requisitesValidator).validateInnJuridical(anyString(), anyString(), any(Errors.class));
        doNothing().when(requisitesValidator).validateOgrn(anyString(), anyString(), any(Errors.class));
        doNothing().when(requisitesValidator).validateKpp(anyString(), anyString(), any(Errors.class));

        // Act
        validator.validate(dto, errors);

        // Assert
        assertFalse(errors.hasErrors(),
                String.format("Не должно быть ошибок для валидного телефона: %s", validPhone));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "abc",
            "1234",
            "+++",
            "123456789012345678901", // слишком длинный
            "!@#$%^&*()"
    })
    @DisplayName("Невалидные форматы телефона должны вызывать ошибку")
    void validate_withInvalidPhoneFormats_shouldHaveErrors(String invalidPhone) {
        // Arrange
        RegistrationDto dto = createValidJuridicalDto();
        dto.setPhone(invalidPhone);
        Errors errors = new BeanPropertyBindingResult(dto, "RegistrationDto");

        // Мокируем успешную валидацию реквизитов
        doNothing().when(requisitesValidator).validateInnJuridical(anyString(), anyString(), any(Errors.class));
        doNothing().when(requisitesValidator).validateOgrn(anyString(), anyString(), any(Errors.class));
        doNothing().when(requisitesValidator).validateKpp(anyString(), anyString(), any(Errors.class));

        // Act
        validator.validate(dto, errors);

        // Assert
        assertTrue(errors.hasErrors(),
                String.format("Должна быть ошибка для невалидного телефона: %s", invalidPhone));
        assertNotNull(errors.getFieldError("phone"));
        assertEquals("phone.invalid.format", errors.getFieldError("phone").getCode());
    }

    @Test
    @DisplayName("DTO с пустым телефоном должен проходить проверку")
    void validate_dtoWithEmptyPhone_shouldNotHaveError() {
        // Arrange
        RegistrationDto dto = createValidJuridicalDto();
        dto.setPhone(null);
        Errors errors = new BeanPropertyBindingResult(dto, "RegistrationDto");

        // Мокируем успешную валидацию реквизитов
        doNothing().when(requisitesValidator).validateInnJuridical(anyString(), anyString(), any(Errors.class));
        doNothing().when(requisitesValidator).validateOgrn(anyString(), anyString(), any(Errors.class));
        doNothing().when(requisitesValidator).validateKpp(anyString(), anyString(), any(Errors.class));

        // Act
        validator.validate(dto, errors);

        // Assert
        assertFalse(errors.hasErrors(), "Не должно быть ошибок при отсутствии телефона");
    }

    // =============================================
    // Тесты для validateRequisitesOnly()
    // =============================================

    @Test
    @DisplayName("validateRequisitesOnly с валидными реквизитами юрлица не должен возвращать ошибки")
    void validateRequisitesOnly_withValidJuridicalRequisites_shouldNotHaveErrors() {
        // Arrange
        RegistrationDto dto = createValidJuridicalDto();
        Errors errors = new BeanPropertyBindingResult(dto, "RegistrationDto");

        // Мокируем успешную валидацию реквизитов
        doNothing().when(requisitesValidator).validateInnJuridical(anyString(), anyString(), any(Errors.class));
        doNothing().when(requisitesValidator).validateOgrn(anyString(), anyString(), any(Errors.class));
        doNothing().when(requisitesValidator).validateKpp(anyString(), anyString(), any(Errors.class));

        // Act
        validator.validateRequisitesOnly(dto, errors);

        // Assert
        assertFalse(errors.hasErrors(), "Не должно быть ошибок при валидации только реквизитов");
        verify(requisitesValidator).validateInnJuridical(eq("7725088527"), eq("inn"), eq(errors));
        verify(requisitesValidator).validateOgrn(eq("1027700229193"), eq("ogrn"), eq(errors));
        verify(requisitesValidator).validateKpp(eq("770501001"), eq("kpp"), eq(errors));
    }

    @Test
    @DisplayName("validateRequisitesOnly с невалидным ИНН должен возвращать ошибку")
    void validateRequisitesOnly_withInvalidInn_shouldHaveErrors() {
        // Arrange
        RegistrationDto dto = createValidJuridicalDto();
        dto.setInn("12345678901"); // 11 цифр - невалидная длина
        Errors errors = new BeanPropertyBindingResult(dto, "RegistrationDto");

        // Act
        validator.validateRequisitesOnly(dto, errors);

        // Assert
        assertTrue(errors.hasErrors(), "Должна быть ошибка при невалидном ИНН");
        assertNotNull(errors.getFieldError("inn"));

        // Проверяем, что валидатор реквизитов не вызывался
        verifyNoInteractions(requisitesValidator);
    }

    // =============================================
    // Тесты для граничных случаев
    // =============================================

    @Test
    @DisplayName("Пустой DTO должен вызывать множественные ошибки")
    void validate_emptyDto_shouldHaveMultipleErrors() {
        // Arrange
        RegistrationDto dto = new RegistrationDto(); // Все поля null
        Errors errors = new BeanPropertyBindingResult(dto, "RegistrationDto");

        // Act
        validator.validate(dto, errors);

        // Assert
        assertTrue(errors.hasErrors(), "Должны быть ошибки для пустого DTO");
        // Проверяем основные обязательные поля
        assertNotNull(errors.getFieldError("inn"));
        assertNotNull(errors.getFieldError("name"));
        assertNotNull(errors.getFieldError("ogrn"));

        // Проверяем, что валидатор реквизитов не вызывался
        verifyNoInteractions(requisitesValidator);
    }

    @Test
    @DisplayName("DTO только с обязательными полями должен проходить базовую проверку")
    void validate_dtoWithOnlyRequiredFields_shouldPassBasicValidation() {
        // Arrange
        RegistrationDto dto = new RegistrationDto();
        dto.setInn("7725088527");
        dto.setName("Тестовая компания");
        dto.setOgrn("1027700229193");
        dto.setKpp("770501001");
        dto.setJurAddress("г. Москва, тестовый адрес");
        // Остальные поля null или пустые

        Errors errors = new BeanPropertyBindingResult(dto, "RegistrationDto");

        // Мокируем успешную валидацию реквизитов
        doNothing().when(requisitesValidator).validateInnJuridical(anyString(), anyString(), any(Errors.class));
        doNothing().when(requisitesValidator).validateOgrn(anyString(), anyString(), any(Errors.class));
        doNothing().when(requisitesValidator).validateKpp(anyString(), anyString(), any(Errors.class));

        // Act
        validator.validate(dto, errors);

        // Assert
        assertFalse(errors.hasErrors(), "Не должно быть ошибок для DTO с минимальным набором полей");
    }

    @ParameterizedTest
    @CsvSource({
            "7725088527, 1027700229193, 770501001, JURIDICAL", // Яндекс
            "500100796259, 304500116000157, '', INDIVIDUAL" // ИП
    })
    @DisplayName("Параметризованный тест разных типов организаций")
    void validate_parameterizedDifferentOrganizationTypes_shouldWorkCorrectly(
            String inn, String ogrn, String kpp, RegistrationDto.OrganizationType expectedType) {
        // Arrange
        RegistrationDto dto = new RegistrationDto();
        dto.setInn(inn);
        dto.setOgrn(ogrn);
        dto.setKpp(kpp.isEmpty() ? null : kpp);
        dto.setName("Test Company");
        dto.setJurAddress("Test Address");

        Errors errors = new BeanPropertyBindingResult(dto, "RegistrationDto");

        // Мокируем успешную валидацию в зависимости от типа
        if (expectedType == RegistrationDto.OrganizationType.JURIDICAL) {
            doNothing().when(requisitesValidator).validateInnJuridical(anyString(), anyString(), any(Errors.class));
            doNothing().when(requisitesValidator).validateOgrn(anyString(), anyString(), any(Errors.class));
            doNothing().when(requisitesValidator).validateKpp(anyString(), anyString(), any(Errors.class));
        } else {
            doNothing().when(requisitesValidator).validateInnIndividual(anyString(), anyString(), any(Errors.class));
            doNothing().when(requisitesValidator).validateOgrnip(anyString(), anyString(), any(Errors.class));
        }

        // Act
        validator.validate(dto, errors);

        // Assert
        assertEquals(expectedType, dto.getOrganizationType(),
                String.format("Тип организации должен определяться корректно для ИНН %s", inn));
    }

    // =============================================
    // Вспомогательные методы
    // =============================================

    private RegistrationDto createValidJuridicalDto() {
        return new RegistrationDto(
                "7725088527",           // ИНН Яндекс (10 цифр)
                "770501001",            // КПП Яндекс
                "1027700229193",        // ОГРН Яндекс (13 цифр)
                "г. Москва, ул. Льва Толстого, 16", // Адрес доставки
                "г. Москва, ул. Льва Толстого, 16", // Юридический адрес
                "Яндекс",               // Краткое название
                "ООО \"Яндекс\"",       // Полное название
                "Иванов Иван Иванович", // Контактное лицо
                "+7(495)739-70-00"      // Телефон
        );
    }

    private RegistrationDto createValidIndividualDto() {
        return new RegistrationDto(
                "500100796259",         // ИНН ИП (12 цифр)
                null,                   // КПП для ИП должен быть null
                "304500116000157",      // ОГРНИП (15 цифр)
                "г. Москва, пр. Мира, 101", // Адрес доставки
                null,                   // Юридический адрес для ИП не обязателен
                "Петров П.П.",          // Краткое название
                "ИП Петров Петр Петрович", // Полное название
                "Петров Петр",          // Контактное лицо
                "+7(495)123-45-67"      // Телефон
        );
    }
}