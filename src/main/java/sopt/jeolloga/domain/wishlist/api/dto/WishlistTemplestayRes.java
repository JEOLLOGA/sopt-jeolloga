package sopt.jeolloga.domain.wishlist.api.dto;

import lombok.Getter;

public record WishlistTemplestayRes(
        Long id,
        String templeName,
        String templestayName,
        String tag,
        String region,
        String type
) {
}
