package com.lakomka.validators;

import com.lakomka.dto.OrderCreationRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
public class OrderCreationValidator implements Validator {

    private static final int MAX_DAYS_FROM_TOMORROW = 14;

    @Override
    public boolean supports(Class<?> clazz) {
        return OrderCreationRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        if (!supports(target.getClass())) {
            errors.reject("orderCreationRequest.invalidType", "Unsupported object type for OrderCreationValidator");
            return;
        }

        OrderCreationRequest req = (OrderCreationRequest) target;
        Date dateDelivery = req.getDateDelivery();

        if (dateDelivery == null) {
            errors.rejectValue("dateDelivery", "dateDelivery.empty", "Укажите дату доставки");
            return;
        }

        // Convert Date -> LocalDate using system default timezone
        Instant instant = dateDelivery.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDate deliveryDate = instant.atZone(zone).toLocalDate();

        LocalDate tomorrow = LocalDate.now(zone).plusDays(1);
        LocalDate maxDate = tomorrow.plusDays(MAX_DAYS_FROM_TOMORROW);

        // Check range: deliveryDate must be between tomorrow and tomorrow+MAX_DAYS_FROM_TOMORROW inclusive
        if (deliveryDate.isBefore(tomorrow) || deliveryDate.isAfter(maxDate)) {
            errors.rejectValue(
                    "dateDelivery",
                    "dateDelivery.invalidRange",
                    new Object[]{tomorrow, maxDate},
                    "Дата доставки должна быть между " + tomorrow + " и " + maxDate + " (включительно)"
            );
            return;
        }

        // Disallow weekends
        DayOfWeek dayOfWeek = deliveryDate.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            errors.rejectValue("dateDelivery", "dateDelivery.weekend", "Доставка не может быть в выходной.");
        }
    }
}
