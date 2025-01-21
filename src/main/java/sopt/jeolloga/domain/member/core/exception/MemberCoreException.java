package sopt.jeolloga.domain.member.core.exception;

import sopt.jeolloga.domain.member.MemberBaseException;
import sopt.jeolloga.exception.ErrorCode;

public class MemberCoreException extends MemberBaseException {
    public MemberCoreException(ErrorCode errorCode) {
        super(errorCode);
    }
}



