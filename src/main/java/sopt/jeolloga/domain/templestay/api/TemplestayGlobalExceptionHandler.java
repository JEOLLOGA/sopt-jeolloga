package sopt.jeolloga.domain.templestay.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import sopt.jeolloga.common.ResponseDto;
import sopt.jeolloga.domain.templestay.TemplestayBaseException;
import sopt.jeolloga.domain.wishlist.core.exception.WishlistCoreException;
import sopt.jeolloga.exception.ErrorCode;

@RestControllerAdvice
public class TemplestayGlobalExceptionHandler {

    @ExceptionHandler(TemplestayBaseException.class)
    public ResponseEntity<ResponseDto<Void>> handleTemplestayBaseException(TemplestayBaseException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ResponseDto.fail(errorCode.getMsg()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseDto<Void>> handleMethodNotAllowedException(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ResponseDto.fail(ErrorCode.METHOD_NOT_ALLOWED.getMsg()));
    }

    @ExceptionHandler(WishlistCoreException.class)
    public ResponseEntity<ResponseDto<Void>> handleWishlistCoreException(WishlistCoreException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ResponseDto.fail("해당 엔터티를 찾을 수 없습니다."));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ResponseDto<Void>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseDto.fail(ErrorCode.INVALID_API.getMsg()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseDto<Void>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        String message = String.format("필수 요청 파라미터가 누락되었습니다: %s", e.getParameterName());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.fail(message));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ResponseDto<Void>> handleNoResourceFoundException(NoResourceFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseDto.fail("요청한 리소스를 찾을 수 없습니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Void>> handleGeneralException(Exception e) {
        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDto.fail("서버 내부 오류입니다."));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseDto<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(ResponseDto.fail("요청 본문이 비어 있거나 잘못되었습니다."));
    }
}
