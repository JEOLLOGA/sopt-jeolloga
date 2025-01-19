package sopt.jeolloga.domain.wishlist.api.dto;

import java.util.List;

public record WishlistTemplestayListRes(
        List<WishlistTemplestayRes> wishlist
) {
    public static WishlistTemplestayListRes of(List<WishlistTemplestayRes> wishlist) {
        return new WishlistTemplestayListRes(wishlist);
    }
}