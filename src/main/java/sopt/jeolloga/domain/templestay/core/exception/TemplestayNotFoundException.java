package sopt.jeolloga.domain.templestay.core.exception;

import sopt.jeolloga.exception.ErrorCode;

public class TemplestayNotFoundException extends TemplestayCoreException {
    public TemplestayNotFoundException() {
        super(ErrorCode.NOT_FOUND_TARGET);
    }
}
