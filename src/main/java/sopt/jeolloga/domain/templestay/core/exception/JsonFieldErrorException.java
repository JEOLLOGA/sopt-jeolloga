package sopt.jeolloga.domain.templestay.core.exception;

import sopt.jeolloga.domain.templestay.TemplestayBaseException;
import sopt.jeolloga.exception.ErrorCode;

public class JsonFieldErrorException extends TemplestayBaseException {
    public JsonFieldErrorException() {
        super(ErrorCode.JSON_FIELD_ERROR);
    }
}
