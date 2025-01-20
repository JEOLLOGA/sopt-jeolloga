package sopt.jeolloga.domain.member.api.dto;

public record MemberDetailRes(Long id, String nickname, String email, String ageRange, String gender, String religion, boolean hasExperience) {
}
