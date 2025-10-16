package com.lakomka.validators;

import com.lakomka.validators.RequisitesValidator.CompanyRequisites;
import com.lakomka.validators.RequisitesValidator.IndividualRequisites;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TypedValidatorTest {

    private RequisitesValidator requisitesValidator;

    @BeforeEach
    void setUp() {
        requisitesValidator = new RequisitesValidator();
    }

    // =============================================
    // Тесты для Validator.forCompanyRequisites()
    // =============================================

    @Test
    @DisplayName("forCompanyRequisites должен создавать валидатор для CompanyRequisites")
    void forCompanyRequisites_shouldCreateValidatorForCompanyRequisites() {
        // Arrange & Act
        Validator validator = RequisitesValidator.forCompanyRequisites((company, errors) -> {
            // Кастомная логика валидации
            if (company.getInn() == null) {
                errors.rejectValue("inn", "inn.required");
            }
        });

        // Assert
        assertNotNull(validator, "Валидатор не должен быть null");
        assertTrue(validator.supports(CompanyRequisites.class),
                "Должен поддерживать CompanyRequisites");
        assertFalse(validator.supports(IndividualRequisites.class),
                "Не должен поддерживать IndividualRequisites");
        assertFalse(validator.supports(Object.class),
                "Не должен поддерживать другие классы");
    }

    @Test
    @DisplayName("forCompanyRequisites должен корректно выполнять валидацию")
    void forCompanyRequisites_shouldExecuteValidationCorrectly() {
        // Arrange
        Validator validator = RequisitesValidator.forCompanyRequisites((company, errors) -> {
            if (company.getInn() != null && company.getInn().length() != 10) {
                errors.rejectValue("inn", "inn.invalid.length");
            }
            if (company.getKpp() == null) {
                errors.rejectValue("kpp", "kpp.required");
            }
        });

        CompanyRequisites company = new CompanyRequisites("123456789", null, null);
        Errors errors = new BeanPropertyBindingResult(company, "company");

        // Act
        validator.validate(company, errors);

        // Assert
        assertTrue(errors.hasErrors(), "Должны быть ошибки валидации");
        assertEquals(2, errors.getErrorCount(), "Должно быть 2 ошибки");
        assertNotNull(errors.getFieldError("inn"));
        assertNotNull(errors.getFieldError("kpp"));
        assertEquals("inn.invalid.length", errors.getFieldError("inn").getCode());
        assertEquals("kpp.required", errors.getFieldError("kpp").getCode());
    }

    @Test
    @DisplayName("forCompanyRequisites с null делегатом должен бросать исключение")
    void forCompanyRequisites_withNullDelegate_shouldThrowException() {
        // Arrange & Act & Assert
        assertThrows(NullPointerException.class,
                () -> RequisitesValidator.forCompanyRequisites(null),
                "Должно бросать NullPointerException при null делегате");
    }

    // =============================================
    // Тесты для Validator.forIndividualRequisites()
    // =============================================

    @Test
    @DisplayName("forIndividualRequisites должен создавать валидатор для IndividualRequisites")
    void forIndividualRequisites_shouldCreateValidatorForIndividualRequisites() {
        // Arrange & Act
        Validator validator = RequisitesValidator.forIndividualRequisites((individual, errors) -> {
            if (individual.getInn() == null) {
                errors.rejectValue("inn", "inn.required");
            }
        });

        // Assert
        assertNotNull(validator, "Валидатор не должен быть null");
        assertTrue(validator.supports(IndividualRequisites.class),
                "Должен поддерживать IndividualRequisites");
        assertFalse(validator.supports(CompanyRequisites.class),
                "Не должен поддерживать CompanyRequisites");
        assertFalse(validator.supports(Object.class),
                "Не должен поддерживать другие классы");
    }

    @Test
    @DisplayName("forIndividualRequisites должен корректно выполнять валидацию")
    void forIndividualRequisites_shouldExecuteValidationCorrectly() {
        // Arrange
        Validator validator = RequisitesValidator.forIndividualRequisites((individual, errors) -> {
            if (individual.getInn() != null && individual.getInn().length() != 12) {
                errors.rejectValue("inn", "inn.invalid.length");
            }
            if (individual.getOgrnip() == null) {
                errors.rejectValue("ogrnip", "ogrnip.required");
            }
        });

        IndividualRequisites individual = new IndividualRequisites("123456789", null);
        Errors errors = new BeanPropertyBindingResult(individual, "individual");

        // Act
        validator.validate(individual, errors);

        // Assert
        assertTrue(errors.hasErrors(), "Должны быть ошибки валидации");
        assertEquals(2, errors.getErrorCount(), "Должно быть 2 ошибки");
        assertNotNull(errors.getFieldError("inn"));
        assertNotNull(errors.getFieldError("ogrnip"));
        assertEquals("inn.invalid.length", errors.getFieldError("inn").getCode());
        assertEquals("ogrnip.required", errors.getFieldError("ogrnip").getCode());
    }

    @Test
    @DisplayName("forIndividualRequisites с null делегатом должен бросать исключение")
    void forIndividualRequisites_withNullDelegate_shouldThrowException() {
        // Arrange & Act & Assert
        assertThrows(NullPointerException.class,
                () -> RequisitesValidator.forIndividualRequisites(null),
                "Должно бросать NullPointerException при null делегате");
    }

    // =============================================
    // Тесты для Validator.forInstanceOf()
    // =============================================

    @Test
    @DisplayName("forInstanceOf должен создавать валидатор для assignable классов")
    void forInstanceOf_shouldCreateValidatorForAssignableClasses() {
        // Arrange & Act
        Validator validator = Validator.forInstanceOf(CompanyRequisites.class, (company, errors) -> {
            if (company.getInn() == null) {
                errors.rejectValue("inn", "inn.required");
            }
        });

        // Assert
        assertNotNull(validator, "Валидатор не должен быть null");
        assertTrue(validator.supports(CompanyRequisites.class),
                "Должен поддерживать CompanyRequisites");
        assertTrue(validator.supports(CompanyRequisitesSubclass.class),
                "Должен поддерживать подклассы CompanyRequisites");
        assertFalse(validator.supports(IndividualRequisites.class),
                "Не должен поддерживать IndividualRequisites");
    }

    @Test
    @DisplayName("forInstanceOf должен корректно выполнять валидацию")
    void forInstanceOf_shouldExecuteValidationCorrectly() {
        // Arrange
        Validator validator = Validator.forInstanceOf(Object.class, (obj, errors) -> {
            errors.reject("general.error", "Общая ошибка");
        });

        CompanyRequisites company = new CompanyRequisites("1234567890", "1234567890123", "123456789");
        Errors errors = new BeanPropertyBindingResult(company, "company");

        // Act
        validator.validate(company, errors);

        // Assert
        assertTrue(errors.hasErrors(), "Должна быть ошибка валидации");
        assertEquals("general.error", errors.getGlobalError().getCode());
    }

    // =============================================
    // Тесты для Validator.forType()
    // =============================================

    @Test
    @DisplayName("forType должен создавать валидатор только для exact классов")
    void forType_shouldCreateValidatorForExactClasses() {
        // Arrange & Act
        Validator validator = Validator.forType(CompanyRequisites.class, (company, errors) -> {
            if (company.getInn() == null) {
                errors.rejectValue("inn", "inn.required");
            }
        });

        // Assert
        assertNotNull(validator, "Валидатор не должен быть null");
        assertTrue(validator.supports(CompanyRequisites.class),
                "Должен поддерживать CompanyRequisites");
        assertFalse(validator.supports(CompanyRequisitesSubclass.class),
                "Не должен поддерживать подклассы CompanyRequisites");
        assertFalse(validator.supports(IndividualRequisites.class),
                "Не должен поддерживать IndividualRequisites");
    }

    @Test
    @DisplayName("forType должен корректно выполнять валидацию")
    void forType_shouldExecuteValidationCorrectly() {
        // Arrange
        Validator validator = Validator.forType(String.class, (str, errors) -> {
            if (str == null || str.trim().isEmpty()) {
                errors.reject("string.empty", "Строка не может быть пустой");
            }
        });

        String testString = "";
        Errors errors = new BeanPropertyBindingResult(testString, "string");

        // Act
        validator.validate(testString, errors);

        // Assert
        assertTrue(errors.hasErrors(), "Должна быть ошибка валидации");
        assertEquals("string.empty", errors.getGlobalError().getCode());
    }

    // =============================================
    // Тесты для validateObject() метода по умолчанию
    // =============================================

    @Test
    @DisplayName("validateObject должен создавать Errors и выполнять валидацию")
    void validateObject_shouldCreateErrorsAndValidate() {
        // Arrange
        Validator validator = RequisitesValidator.forCompanyRequisites((company, errors) -> {
            if (company.getInn() == null) {
                errors.rejectValue("inn", "inn.required");
            }
        });

        CompanyRequisites company = new CompanyRequisites(null, null, null);

        // Act
        Errors errors = validator.validateObject(company);

        // Assert
        assertNotNull(errors, "Errors не должен быть null");
        assertTrue(errors.hasErrors(), "Должны быть ошибки валидации");
        assertNotNull(errors.getFieldError("inn"));
        assertEquals("inn.required", errors.getFieldError("inn").getCode());
    }

    @Test
    @DisplayName("validateObject с валидными данными не должен возвращать ошибки")
    void validateObject_withValidData_shouldNotReturnErrors() {
        // Arrange
        Validator validator = RequisitesValidator.forCompanyRequisites((company, errors) -> {
            // Все данные валидны - не добавляем ошибок
        });

        CompanyRequisites company = new CompanyRequisites("1234567890", "1234567890123", "123456789");

        // Act
        Errors errors = validator.validateObject(company);

        // Assert
        assertNotNull(errors, "Errors не должен быть null");
        assertFalse(errors.hasErrors(), "Не должно быть ошибок валидации");
    }

    // =============================================
    // Интеграционные тесты с реальной логикой валидации
    // =============================================

    @Test
    @DisplayName("forCompanyRequisites с реальной логикой валидации реквизитов")
    void forCompanyRequisites_withRealValidationLogic_shouldWorkCorrectly() {
        // Arrange
        Validator validator = RequisitesValidator.forCompanyRequisites((company, errors) -> {
            requisitesValidator.validateInnJuridical(company.getInn(), "inn", errors);
            requisitesValidator.validateOgrn(company.getOgrn(), "ogrn", errors);
            requisitesValidator.validateKpp(company.getKpp(), "kpp", errors);
        });

        CompanyRequisites validCompany = new CompanyRequisites(
                "7725088527", "1027700229193", "770501001");
        CompanyRequisites invalidCompany = new CompanyRequisites(
                "1234567890", "1234567890123", "123456789");

        // Act & Assert для валидных данных
        Errors validErrors = validator.validateObject(validCompany);
        assertFalse(validErrors.hasErrors(),
                "Не должно быть ошибок для валидных реквизитов");

        // Act & Assert для невалидных данных
        Errors invalidErrors = validator.validateObject(invalidCompany);
        assertTrue(invalidErrors.hasErrors(),
                "Должны быть ошибки для невалидных реквизитов");
    }

    // =============================================
    // Тесты для проверки типизации и кастов
    // =============================================

    @Test
    @DisplayName("Валидация неподдерживаемого типа должна вызывать ClassCastException")
    void validate_withUnsupportedType_shouldThrowClassCastException() {
        // Arrange
        Validator validator = RequisitesValidator.forCompanyRequisites((company, errors) -> {
            errors.rejectValue("inn", "inn.required");
        });

        IndividualRequisites individual = new IndividualRequisites("123456789012", "123456789012345");
        Errors errors = new BeanPropertyBindingResult(individual, "individual");

        // Act & Assert
        assertThrows(ClassCastException.class, () -> validator.validate(individual, errors),
                "Должно бросать ClassCastException для неподдерживаемого типа");

    }

    @Test
    @DisplayName("validateObject с null объектом должен бросать исключение")
    void validateObject_withNullObject_shouldThrowException() {
        // Arrange
        Validator validator = RequisitesValidator.forCompanyRequisites((company, errors) -> {
            // Логика валидации
        });

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> validator.validateObject(null),
                "Должно бросать IllegalArgumentException при null объекте");
    }

    // =============================================
    // Тесты для проверки делегата с разной логикой
    // =============================================

    @Test
    @DisplayName("Делегат с комплексной бизнес-логикой должен работать корректно")
    void validator_withComplexBusinessLogic_shouldWorkCorrectly() {
        // Arrange
        Validator validator = RequisitesValidator.forCompanyRequisites((company, errors) -> {
            // Проверка ИНН
            if (company.getInn() == null) {
                errors.rejectValue("inn", "inn.required");
            } else if (company.getInn().length() != 10) {
                errors.rejectValue("inn", "inn.invalid.length");
            }

            // Проверка ОГРН
            if (company.getOgrn() == null) {
                errors.rejectValue("ogrn", "ogrn.required");
            } else if (company.getOgrn().length() != 13) {
                errors.rejectValue("ogrn", "ogrn.invalid.length");
            }

            // Проверка КПП
            if (company.getKpp() == null) {
                errors.rejectValue("kpp", "kpp.required");
            } else if (company.getKpp().length() != 9) {
                errors.rejectValue("kpp", "kpp.invalid.length");
            }

            // Комплексная проверка
            if (company.getInn() != null && company.getKpp() != null) {
                if (!company.getInn().substring(0, 4).equals(company.getKpp().substring(0, 4))) {
                    errors.reject("inn.kpp.mismatch", "Коды региона в ИНН и КПП не совпадают");
                }
            }
        });

        CompanyRequisites company = new CompanyRequisites(
                "123456789", // 9 цифр - невалидно
                "1234567890123", // 13 цифр - валидно по длине
                "999901001" // КПП с другим регионом
        );

        // Act
        Errors errors = validator.validateObject(company);

        // Assert
        assertTrue(errors.hasErrors(), "Должны быть ошибки валидации");
        assertNotNull(errors.getFieldError("inn"));
        assertNotNull(errors.getGlobalError());
        assertEquals("inn.invalid.length", errors.getFieldError("inn").getCode());
        assertEquals("inn.kpp.mismatch", errors.getGlobalError().getCode());
    }

    @Test
    @DisplayName("Несколько валидаторов для разных типов должны работать независимо")
    void multipleValidatorsForDifferentTypes_shouldWorkIndependently() {
        // Arrange
        Validator companyValidator = RequisitesValidator.forCompanyRequisites((company, errors) -> {
            if (company.getKpp() == null) {
                errors.rejectValue("kpp", "kpp.required");
            }
        });

        Validator individualValidator = RequisitesValidator.forIndividualRequisites((individual, errors) -> {
            if (individual.getOgrnip() == null) {
                errors.rejectValue("ogrnip", "ogrnip.required");
            }
        });

        CompanyRequisites company = new CompanyRequisites("1234567890", "1234567890123", null);
        IndividualRequisites individual = new IndividualRequisites("123456789012", null);

        // Act
        Errors companyErrors = companyValidator.validateObject(company);
        Errors individualErrors = individualValidator.validateObject(individual);

        // Assert
        assertTrue(companyErrors.hasErrors(), "Должны быть ошибки для компании");
        assertNotNull(companyErrors.getFieldError("kpp"));

        assertTrue(individualErrors.hasErrors(), "Должны быть ошибки для ИП");
        assertNotNull(individualErrors.getFieldError("ogrnip"));
    }

    // =============================================
    // Вспомогательные классы для тестирования наследования
    // =============================================

    static class CompanyRequisitesSubclass extends CompanyRequisites {
        public CompanyRequisitesSubclass(String inn, String ogrn, String kpp) {
            super(inn, ogrn, kpp);
        }
    }
}
