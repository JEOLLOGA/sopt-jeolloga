package sopt.jeolloga.common;

public record ResponseDto<T>(
        String code,
        T data,
        String msg
) {
    public static <T> ResponseDto<T> fail(String code, String msg) {
        return new ResponseDto<>(code, null, msg);
    }

    public static <T> ResponseDto<T> failValidate(final T data) {
        return new ResponseDto<>("fail", data, null);
    }

    public static <T> ResponseDto<T> success(final T data) {
        return new ResponseDto<>(null, data, null);
    }
}
