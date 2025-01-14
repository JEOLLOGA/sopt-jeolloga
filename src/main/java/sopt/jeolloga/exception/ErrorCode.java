package sopt.jeolloga.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    BAD_REQUEST(40000, HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    JSON_FIELD_ERROR(40005, HttpStatus.BAD_REQUEST, "jSON 오류 혹은 Request Body 필드 오류입니다."),

    NOT_FOUND_TEMPLESTAY(40400, HttpStatus.NOT_FOUND, "대상을 찾을 수 없습니다."),
    INVALID_API(40401, HttpStatus.NOT_FOUND, "잘못된 API입니다."),
    FILTERED_TEMPLESTAY_NOT_FOUND(40403, HttpStatus.NOT_FOUND, "필터가 적용된 템플스테이를 찾을 수 없습니다."),

    METHOD_NOT_ALLOWED(40500, HttpStatus.METHOD_NOT_ALLOWED, "잘못된 HTTP Method 요청입니다."),

    INTERNAL_SERVER_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");

    private final int code;
    @JsonIgnore
    private final HttpStatus httpStatus;
    private final String msg;

    ErrorCode(int code, HttpStatus httpStatus, String msg) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.msg = msg;
    }
}
