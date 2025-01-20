package sopt.jeolloga.domain.templestay.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FilterListRes(List<TemplestayRes> templestays) {
}
