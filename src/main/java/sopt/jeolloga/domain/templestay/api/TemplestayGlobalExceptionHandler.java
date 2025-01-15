package sopt.jeolloga.domain.templestay.api;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
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

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseDto<Void>> handleEntityNotFoundException(EntityNotFoundException e) {
        ResponseDto<Void> response = new ResponseDto<>(null, "해당 엔터티를 찾을 수 없습니다.");
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(WishlistCoreException.class)
    public ResponseEntity<ResponseDto<Void>> handleWishlistCoreException(WishlistCoreException e) {
        ErrorCode errorCode = e.getErrorCode();
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
