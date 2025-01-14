package sopt.jeolloga.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "error", "존재하지 않는 사용자(User)입니다."),
    NOT_FOUND_TEMPLESTAY(HttpStatus.NOT_FOUND, "error", "존재하지 않는 템플스테이입니다.")
    ;

    @JsonIgnore
    private final HttpStatus httpStatus;
    private final String code;
    private final String msg;

    ErrorCode(HttpStatus httpStatus, String code, String msg) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.msg = msg;
    }
}
