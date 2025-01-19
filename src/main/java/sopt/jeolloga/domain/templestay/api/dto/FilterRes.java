package sopt.jeolloga.domain.templestay.api.dto;

import java.util.List;
import java.util.Map;

public record FilterRes(Map<String, List<String>> filters) {
}