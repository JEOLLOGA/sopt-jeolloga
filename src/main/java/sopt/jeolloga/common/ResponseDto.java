package sopt.jeolloga.common;

public record ResponseDto<T>(
        int code,
        T data,
        String msg
) {
    public static <T> ResponseDto<T> fail(int code, String msg) {
        return new ResponseDto<>(code, null, msg);
    }

    public static <T> ResponseDto<T> failValidate(final T data) {
        return new ResponseDto<>(400, data, null);
    }

    public static <T> ResponseDto<T> success(final T data) {
        return new ResponseDto<>(200, data, null);
    }
}
