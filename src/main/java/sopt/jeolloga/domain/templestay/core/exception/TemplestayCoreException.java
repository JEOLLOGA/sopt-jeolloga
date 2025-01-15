package sopt.jeolloga.domain.templestay.core.exception;

import sopt.jeolloga.domain.templestay.TemplestayBaseException;
import sopt.jeolloga.exception.ErrorCode;

public class TemplestayCoreException extends TemplestayBaseException {
    public TemplestayCoreException(ErrorCode errorCode) {
        super(errorCode);
    }
}
