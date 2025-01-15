package sopt.jeolloga.domain.wishlist;

import lombok.Getter;
import sopt.jeolloga.exception.ErrorCode;

@Getter
public class WishlistBaseException extends RuntimeException {
    private final ErrorCode errorCode;

    public WishlistBaseException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
    }
}
