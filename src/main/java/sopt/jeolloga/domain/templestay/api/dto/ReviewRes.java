package sopt.jeolloga.domain.templestay.api.dto;

public record ReviewRes(
        Long reviewId,
        String reviewTitle,
        String reviewLink,
        String reviewName,
        String reviewDescription,
        String reviewDate,
        String reviewImgUrl
) {
}