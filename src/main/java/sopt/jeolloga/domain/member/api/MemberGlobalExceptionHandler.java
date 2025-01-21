package sopt.jeolloga.domain.member.api;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sopt.jeolloga.common.ResponseDto;
import sopt.jeolloga.domain.member.MemberBaseException;
import sopt.jeolloga.domain.member.core.exception.MemberCoreException;
import sopt.jeolloga.exception.ErrorCode;

@RestControllerAdvice
public class MemberGlobalExceptionHandler {

    @ExceptionHandler(MemberBaseException.class)
    public ResponseEntity<ResponseDto<Void>>handlerMemberBaseException(MemberBaseException e){
        ErrorCode errorCode = e.getErrorCode();
        ResponseDto<Void> response = new ResponseDto<>(null, errorCode.getMsg());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(MemberCoreException.class)
    public ResponseEntity<ResponseDto<Void>>handlerMemberCoreException(MemberCoreException e){
        ErrorCode errorCode = e.getErrorCode();
        ResponseDto<Void> response = new ResponseDto<>(null, errorCode.getMsg());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }



    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseDto<Void>>handlerHttpMessageNotReadableException(HttpMessageNotReadableException e){
        e.printStackTrace();
        ResponseDto<Void> response = new ResponseDto<>(null, "Request Body가 올바르지 않습니다.");
        return ResponseEntity.status(400).body(response);
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseDto<Void>>handlerHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e){
        e.printStackTrace();
        ResponseDto<Void> response = new ResponseDto<>(null, "지원하지 않는 Request Method입니다.");
        return ResponseEntity.status(400).body(response);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ResponseDto<Void>>handlerMissingRequestHeaderException(MissingRequestHeaderException e){
        e.printStackTrace();
        ResponseDto<Void> response = new ResponseDto<>(null, "Request Header가 올바르지 않습니다.");
        return ResponseEntity.status(400).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Void>> handlerGeneralException(Exception e) {
        e.printStackTrace();
        ResponseDto<Void> response = new ResponseDto<>(null, "서버 내부 오류 입니다.");
        return ResponseEntity.status(500).body(response);
    }
}
