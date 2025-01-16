package sopt.jeolloga.domain.wishlist.api.dto;

import java.util.List;

public record WishlistListRes(
        List<WishlistTemplestayRes> wishlist
) {
    public static WishlistListRes of(List<WishlistTemplestayRes> wishlist) {
        return new WishlistListRes(wishlist);
    }
}