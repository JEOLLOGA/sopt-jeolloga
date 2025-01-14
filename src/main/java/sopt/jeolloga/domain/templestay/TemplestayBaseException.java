package sopt.jeolloga.domain.templestay;

import lombok.Getter;
import sopt.jeolloga.exception.ErrorCode;

@Getter
public class TemplestayBaseException extends RuntimeException {
    private final ErrorCode errorCode;

    public TemplestayBaseException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
    }
}
