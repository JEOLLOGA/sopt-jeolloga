package sopt.jeolloga.domain.templestay.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sopt.jeolloga.common.ResponseDto;
import sopt.jeolloga.domain.templestay.TemplestayBaseException;
import sopt.jeolloga.exception.ErrorCode;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TemplestayBaseException.class)
    public ResponseEntity<ResponseDto<Void>> handlerTemplestayBaseException(TemplestayBaseException e) {
        ErrorCode errorCode = e.getErrorCode();
        ResponseDto<Void> response = new ResponseDto<>(errorCode.getCode(), null, errorCode.getMsg());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Void>> handlerGeneralException(Exception e) {
        e.printStackTrace();
        ResponseDto<Void> response = new ResponseDto<>(5000, null, "서버 내부 오류 입니다.");
        return ResponseEntity.status(500).body(response);
    }
}
