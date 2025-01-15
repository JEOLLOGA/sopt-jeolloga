package sopt.jeolloga.domain.wishlist.api.controller;

import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import sopt.jeolloga.common.ResponseDto;
import sopt.jeolloga.domain.wishlist.api.service.WishlistService;

@RestController
@RequiredArgsConstructor
public class WishlistController {
    private final WishlistService wishlistService;

    @PostMapping("/user/templestay/liked/{templestayId}")
    public ResponseEntity<ResponseDto<?>> addWishlist(
            @PathVariable("templestayId") @NotNull Long templestayId,
            @RequestHeader("userId") @NotNull Long userId) {
        wishlistService.addWishlist(userId, templestayId);
        return ResponseEntity.ok(ResponseDto.success("success"));
    }
}
