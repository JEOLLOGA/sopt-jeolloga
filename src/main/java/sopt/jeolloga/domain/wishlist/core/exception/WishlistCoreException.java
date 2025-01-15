package sopt.jeolloga.domain.wishlist.core.exception;

import sopt.jeolloga.domain.wishlist.WishlistBaseException;
import sopt.jeolloga.exception.ErrorCode;

public class WishlistCoreException extends WishlistBaseException {
    public WishlistCoreException(ErrorCode errorCode) {
        super(errorCode);
    }
}
