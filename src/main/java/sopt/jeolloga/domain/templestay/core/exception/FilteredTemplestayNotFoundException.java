package sopt.jeolloga.domain.templestay.core.exception;

import sopt.jeolloga.domain.templestay.TemplestayBaseException;
import sopt.jeolloga.exception.ErrorCode;

public class FilteredTemplestayNotFoundException extends TemplestayBaseException {
    public FilteredTemplestayNotFoundException() {
        super(ErrorCode.FILTERED_TEMPLESTAY_NOT_FOUND);
    }
}
