package sopt.jeolloga.domain.wishlist.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.common.ResponseDto;
import sopt.jeolloga.domain.wishlist.api.service.WishlistService;

@RestController
@RequiredArgsConstructor
public class WishlistController {
    private final WishlistService wishlistService;

    @PostMapping("/user/templestay/liked/{templestayId}")
    public ResponseEntity<ResponseDto<?>> addWishlist(
            @PathVariable("templestayId") Long templestayId,
            @RequestHeader("userId") Long userId) {
        wishlistService.addWishlist(userId, templestayId);
        return ResponseEntity.ok(ResponseDto.success("success"));
    }

    @DeleteMapping("/user/templestay/liked/delete/{templestayId}")
    public ResponseEntity<ResponseDto<?>> deleteWishlist(
            @PathVariable("templestayId") Long templestayId,
            @RequestHeader("userId") Long userId) {
        wishlistService.deleteWishlist(userId, templestayId);
        return ResponseEntity.ok(ResponseDto.success("success"));
    }
}
