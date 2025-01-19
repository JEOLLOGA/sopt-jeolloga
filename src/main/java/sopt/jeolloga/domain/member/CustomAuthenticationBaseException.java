package sopt.jeolloga.domain.member;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;
import sopt.jeolloga.exception.ErrorCode;

@Getter
public class CustomAuthenticationBaseException extends AuthenticationException {

    private final ErrorCode errorCode;

    public CustomAuthenticationBaseException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
    }
}
