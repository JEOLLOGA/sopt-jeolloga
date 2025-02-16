package sopt.jeolloga.domain.member.api.dto;

public record TokenRes(String token_type, String access_token, Integer expires_in, String refresh_token, Integer refresh_token_expires_in) {
}
