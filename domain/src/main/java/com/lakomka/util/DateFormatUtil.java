package com.lakomka.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateFormatUtil {

    // Predefined thread-safe formatters
    public static final DateTimeFormatter DEFAULT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter SHORT_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter WITH_SECONDS_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

    public static String formatDate(java.util.Date date, DateTimeFormatter formatter) {
        if (date instanceof java.sql.Date) {
            return formatSqlDate((java.sql.Date) date, formatter);
        } else {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .format(formatter);
        }
    }

    public static String formatSqlDate(java.sql.Date date, DateTimeFormatter formatter) {
        LocalDate localDate = date.toLocalDate();
        return localDate.format(formatter);
    }

    public static String formatDate(LocalDate localDate, DateTimeFormatter formatter) {
        return localDate.format(formatter);
    }

    public static String formatDate(LocalDateTime localDateTime, DateTimeFormatter formatter) {
        return localDateTime.format(formatter);
    }

}
