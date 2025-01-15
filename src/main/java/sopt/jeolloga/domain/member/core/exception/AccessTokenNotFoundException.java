package sopt.jeolloga.domain.member.core.exception;

import sopt.jeolloga.domain.member.MemberBaseException;
import sopt.jeolloga.exception.ErrorCode;

public class AccessTokenNotFoundException extends MemberBaseException {
    public AccessTokenNotFoundException() {
        super(ErrorCode.MISSING_ACCESS_TOKEN);
    }
}
