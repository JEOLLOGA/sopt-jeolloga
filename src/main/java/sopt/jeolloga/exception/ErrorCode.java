package sopt.jeolloga.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 400번대: 클라이언트 요청 오류
    BAD_REQUEST(40000, HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    MISSING_TARGET_NAME(40001, HttpStatus.BAD_REQUEST, "템플스테이 이름이 누락되었습니다."),
    MISSING_TITLE(40002, HttpStatus.BAD_REQUEST, "블로그 제목이 누락되었습니다."),
    MISSING_ACCESS_TOKEN(40003, HttpStatus.BAD_REQUEST, "액세스 토큰이 누락되었습니다"),
    INVALID_SEARCH_CONTENT(40004, HttpStatus.BAD_REQUEST, "검색어가 비어 있습니다."),
    JSON_FIELD_ERROR(40005, HttpStatus.BAD_REQUEST, "JSON 오류 혹은 Request Body 필드 오류입니다."),
    INVALID_DATE_FORMAT(40006, HttpStatus.BAD_REQUEST, "날짜 형식이 잘못되었습니다."),
    DUPLICATE_WISHLIST(40007, HttpStatus.BAD_REQUEST, "중복된 Wishlist 값입니다."),
    MISSING_USER_ID(40008, HttpStatus.BAD_REQUEST, "userId가 없습니다."),
    BAD_REQUEST_PARAMETER(40009, HttpStatus.BAD_REQUEST, "필수 요청 파라미터가 누락되었습니다."),


    // 401번대: 권한 오류
    UNAUTHORIZED(40100,HttpStatus.UNAUTHORIZED,"서비스 이용 권한이 없습니다"),
    EXPIRED_ACCESS_TOKEN(40101, HttpStatus.UNAUTHORIZED, "액세스 토큰이 만료되었습니다"),
    EXPIRED_REFRESH_TOKEN(40102, HttpStatus.UNAUTHORIZED, "리프레쉬 토큰이 만료되었습니다"),
    EXPIRED_TOKEN(40103, HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다"),
    INVALID_TOKEN(40104,HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰이 사용되고 있습니다"),

    // 404번대: 리소스 찾기 오류
    NOT_FOUND_TARGET(40400, HttpStatus.NOT_FOUND, "대상을 찾을 수 없습니다."),
    INVALID_API(40401, HttpStatus.NOT_FOUND, "잘못된 API입니다."),
    NOT_FOUND_USER(40402, HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    FILTERED_TEMPLESTAY_NOT_FOUND(40403, HttpStatus.NOT_FOUND, "필터가 적용된 템플스테이를 찾을 수 없습니다."),
    REVIEW_NOT_FOUND(40404, HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),
    NOT_FOUND_TEMPLESTAY(40405, HttpStatus.NOT_FOUND, "존재하지 않는 템플스테이 ID입니다."),

    // 405번대: HTTP 메서드 오류
    METHOD_NOT_ALLOWED(40500, HttpStatus.METHOD_NOT_ALLOWED, "잘못된 HTTP Method 요청입니다."),

    // 500번대: 서버 내부 오류
    INTERNAL_SERVER_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    API_CALL_FAILED(50001, HttpStatus.INTERNAL_SERVER_ERROR, "API 호출에 실패했습니다.");

    private final int code;
    @JsonIgnore
    private final HttpStatus httpStatus;
    private final String msg;

    ErrorCode(int code, HttpStatus httpStatus, String msg) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.msg = msg;
    }
}
