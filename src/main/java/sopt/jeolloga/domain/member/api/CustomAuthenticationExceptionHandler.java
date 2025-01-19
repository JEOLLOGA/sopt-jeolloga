package sopt.jeolloga.domain.member.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sopt.jeolloga.common.ResponseDto;
import sopt.jeolloga.domain.member.CustomAuthenticationBaseException;
import sopt.jeolloga.domain.member.core.exception.InvalidAccessTokenException;
import sopt.jeolloga.exception.ErrorCode;

@RestControllerAdvice
public class CustomAuthenticationExceptionHandler {

    @ExceptionHandler(CustomAuthenticationBaseException.class)
    public ResponseEntity<ResponseDto<Void>>handlerMemberBaseException(CustomAuthenticationBaseException e){
        ErrorCode errorCode = e.getErrorCode();
        ResponseDto<Void> response = new ResponseDto<>(null, errorCode.getMsg());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(InvalidAccessTokenException.class)
    public ResponseEntity<ResponseDto<Void>> handlerAccesssTokenNotFound(InvalidAccessTokenException e){
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
