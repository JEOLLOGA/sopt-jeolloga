package sopt.jeolloga.domain.templestay.api.dto;

public record ReviewDto(
        String templeName,
        String reviewTitle,
        String reviewDescription,
        String reviewName,
        String reviewDate,
        String reviewRink,
        String reviewImgUrl
) {
}

