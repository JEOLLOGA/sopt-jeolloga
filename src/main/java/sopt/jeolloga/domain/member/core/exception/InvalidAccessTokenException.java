package sopt.jeolloga.domain.member.core.exception;

import sopt.jeolloga.domain.member.MemberBaseException;
import sopt.jeolloga.exception.ErrorCode;

public class InvalidAccessTokenException extends MemberBaseException {
    public InvalidAccessTokenException() {
        super(ErrorCode.UNAUTHORIZED);
    }
}
