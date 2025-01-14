package sopt.jeolloga.domain.templestay.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpRequestMethodNotSupportedException;
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
        ResponseDto<Void> response = new ResponseDto<>(null, errorCode.getMsg());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseDto<Void>> handleMethodNotAllowedException(HttpRequestMethodNotSupportedException ex) {
        ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;
        ResponseDto<Void> response = new ResponseDto<>(null, errorCode.getMsg());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Void>> handlerGeneralException(Exception e) {
        e.printStackTrace();
        ResponseDto<Void> response = new ResponseDto<>(null, "서버 내부 오류 입니다.");
        return ResponseEntity.status(500).body(response);
    }
}
