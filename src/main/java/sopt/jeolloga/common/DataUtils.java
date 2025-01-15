package sopt.jeolloga.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DataUtils {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public static String formatDate(LocalDate date) {
        return date.format(formatter);
    }

    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, formatter);
    }
}
