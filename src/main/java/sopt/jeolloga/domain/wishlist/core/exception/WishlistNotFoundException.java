package sopt.jeolloga.domain.wishlist.core.exception;

import sopt.jeolloga.domain.wishlist.core.exception.WishlistCoreException;
import sopt.jeolloga.exception.ErrorCode;

public class WishlistNotFoundException extends WishlistCoreException {
    public WishlistNotFoundException() {
        super(ErrorCode.NOT_FOUND_TARGET);
    }
}
