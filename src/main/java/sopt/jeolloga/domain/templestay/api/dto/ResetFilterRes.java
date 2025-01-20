package sopt.jeolloga.domain.templestay.api.dto;

import java.util.Map;

public record ResetFilterRes(long count, Map<String, Object> reset) {
}
