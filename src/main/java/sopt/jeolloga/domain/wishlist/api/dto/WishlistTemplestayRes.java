package sopt.jeolloga.domain.wishlist.api.dto;

import lombok.Getter;

public record WishlistTemplestayRes(
        Long templestayId,
        String templeName,
        String templestayName,
        String tag,
        String region,
        String type,
        String imgUrl,
        boolean liked
) {
}
