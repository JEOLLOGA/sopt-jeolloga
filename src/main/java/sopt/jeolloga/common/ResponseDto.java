package sopt.jeolloga.common;

public record ResponseDto<T>(
        T data,
        String msg
) {
    public static <T> ResponseDto<T> fail(String msg) {
        return new ResponseDto<>(null, msg);
    }

    public static <T> ResponseDto<T> failValidate(final T data) {
        return new ResponseDto<>(data, null);
    }

    public static <T> ResponseDto<T> success(final T data) {
        return new ResponseDto<>(data, null);
    }
}
