package sopt.jeolloga.domain.templestay.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import sopt.jeolloga.common.ResponseDto;
import sopt.jeolloga.domain.templestay.TemplestayBaseException;
import sopt.jeolloga.domain.wishlist.core.exception.WishlistCoreException;
import sopt.jeolloga.exception.ErrorCode;


@RestControllerAdvice
public class TemplestayGlobalExceptionHandler {
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

    @ExceptionHandler(WishlistCoreException.class)
    public ResponseEntity<ResponseDto<Void>> handleWishlistCoreException(WishlistCoreException e) {
        ErrorCode errorCode = e.getErrorCode();
        ResponseDto<Void> response = new ResponseDto<>(null, "해당 엔터티를 찾을 수 없습니다.");
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ResponseDto<Void>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        ErrorCode errorCode = ErrorCode.INVALID_API;
        ResponseDto<Void> response = ResponseDto.fail(errorCode.getMsg());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseDto<Void>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        String message = String.format("필수 요청 파라미터가 누락되었습니다: %s", e.getParameterName());
        ResponseDto<Void> response = ResponseDto.fail(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Void>> handlerGeneralException(Exception e) {
        e.printStackTrace();
        ResponseDto<Void> response = new ResponseDto<>(null, "서버 내부 오류 입니다.");
        return ResponseEntity.status(500).body(response);
    }
}
