package sopt.jeolloga.domain.member.core.exception;

import sopt.jeolloga.domain.member.CustomAuthenticationBaseException;
import sopt.jeolloga.exception.ErrorCode;

public class CustomAuthenticationCoreException extends CustomAuthenticationBaseException {
    public CustomAuthenticationCoreException(ErrorCode errorCode) {
        super(errorCode);
    }
}



