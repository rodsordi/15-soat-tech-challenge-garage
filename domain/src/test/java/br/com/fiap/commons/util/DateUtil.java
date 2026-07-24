package br.com.fiap.commons.util;

import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.ZoneId.systemDefault;
import static java.time.format.DateTimeFormatter.ofPattern;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class DateUtil {

    public static final DateTimeFormatter DF = ofPattern("dd/MM/yyyy")
            .withZone(systemDefault());

    public static final DateTimeFormatter DTF = ofPattern("dd/MM/yyyy HH:mm:ss")
            .withZone(systemDefault());

    public static final DateTimeFormatter DTF3 = ofPattern("dd/MM/yyyy HH:mm:ss.SSS")
            .withZone(systemDefault());

    private static final DateTimeFormatter GENERIC_DTF = ofPattern("dd/MM/yyyy HH:mm:ss" +
            "[.SSSSSSSSS]" +
            "[.SSSSSSSS]" +
            "[.SSSSSSS]" +
            "[.SSSSSS]" +
            "[.SSSSS]" +
            "[.SSSS]" +
            "[.SSS]" +
            "[.SS]" +
            "[.S]").withZone(systemDefault());

    public static LocalDate newDate(String text, DateTimeFormatter dtf) {
        return LocalDate.parse(text, dtf);
    }

    public static LocalDate newDate(String text, String pattern) {
        return newDate(text, ofPattern(pattern));
    }

    public static LocalDate newDate(String text) {
        return newDate(text, DF);
    }

    public static LocalDateTime newDateTime(String text, DateTimeFormatter dtf) {
        return LocalDateTime.parse(text, dtf);
    }

    public static LocalDateTime newDateTime(String text, String pattern) {
        return newDateTime(text, ofPattern(pattern));
    }

    public static LocalDateTime newDateTime(String text) {
        return newDateTime(text, GENERIC_DTF);
    }
}
