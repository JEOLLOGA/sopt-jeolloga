package sopt.jeolloga.domain.member.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoUserInfoRes(
        Long id,
        boolean has_signed_up,
        Date connected_at,
        Date synched_at,
        Map<String, String> properties,
        KakaoAccount kakao_account,
        Partner for_partner
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record KakaoAccount(
            boolean profile_needs_agreement,
            boolean profile_nickname_needs_agreement,
            boolean profile_image_needs_agreement,
            Profile profile,
            boolean name_needs_agreement,
            String name,
            boolean email_needs_agreement,
            boolean is_email_valid,
            boolean is_email_verified,
            String email,
            boolean age_range_needs_agreement,
            String age_range,
            boolean birthyear_needs_agreement,
            String birthyear,
            boolean birthday_needs_agreement,
            String birthday,
            String birthday_type,
            boolean gender_needs_agreement,
            String gender,
            boolean phone_number_needs_agreement,
            String phone_number,
            boolean ci_needs_agreement,
            String ci,
            Date ci_authenticated_at
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Profile(
            String nickname,
            String thumbnail_image_url,
            String profile_image_url,
            boolean is_default_image,
            boolean is_default_nickname
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Partner(
            String uuid
    ) {}
}
