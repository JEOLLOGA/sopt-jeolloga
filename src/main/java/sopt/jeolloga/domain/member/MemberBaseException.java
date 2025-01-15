package sopt.jeolloga.domain.member;

import lombok.Getter;
import sopt.jeolloga.exception.ErrorCode;

@Getter
public class MemberBaseException extends RuntimeException {
    private final ErrorCode errorCode;

    public MemberBaseException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
    }
}