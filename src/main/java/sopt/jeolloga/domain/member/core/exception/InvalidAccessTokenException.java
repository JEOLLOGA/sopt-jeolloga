package sopt.jeolloga.domain.member.core.exception;

import sopt.jeolloga.domain.member.CustomAuthenticationBaseException;
import sopt.jeolloga.exception.ErrorCode;

public class InvalidAccessTokenException extends CustomAuthenticationBaseException {
    public InvalidAccessTokenException() {
        super(ErrorCode.UNAUTHORIZED);
    }
}
