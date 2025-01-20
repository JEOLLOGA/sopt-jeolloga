package sopt.jeolloga.common;

import sopt.jeolloga.domain.templestay.core.exception.TemplestayCoreException;
import sopt.jeolloga.exception.ErrorCode;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class DataUtils {

    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    private DataUtils() {
    }

    public static String formatReviewDate(String reviewDate) {
        try {
            LocalDate date = LocalDate.parse(reviewDate, INPUT_FORMATTER);
            return date.format(OUTPUT_FORMATTER);
        } catch (Exception e) {
            throw new TemplestayCoreException(ErrorCode.INVALID_DATE_FORMAT);
        }
    }

    public static String convertPriceToString(Integer priceCode) {
        if (priceCode == null || priceCode <= 0) {
            return null;
        }
        return String.format("%,d원", priceCode);
    }

    public static String extractFirstTwoWords(String fullAddress) {
        if (fullAddress == null || fullAddress.isBlank()) {
            return null;
        }
        String[] words = fullAddress.split(" ");
        if (words.length < 2) {
            return fullAddress; // 두 단어 미만일 경우 전체 주소 반환
        }
        return String.join(" ", Arrays.copyOfRange(words, 0, 2));
    }
}
