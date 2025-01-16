package sopt.jeolloga.domain.wishlist.api.dto;

import java.util.List;

public record PageWishlistRes<T>(
        int page,
        int pageSize,
        int totalPages,
        List<T> wishlist
) {
}
