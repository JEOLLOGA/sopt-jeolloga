package sopt.jeolloga.domain.wishlist.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.common.ResponseDto;
import sopt.jeolloga.domain.wishlist.api.dto.PageWishlistRes;
import sopt.jeolloga.domain.wishlist.api.dto.WishlistTemplestayListRes;
import sopt.jeolloga.domain.wishlist.api.dto.WishlistTemplestayRes;
import sopt.jeolloga.domain.wishlist.api.service.WishlistService;

import java.util.List;

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

    @GetMapping("/user/wishlist")
    public PageWishlistRes<WishlistTemplestayRes> getWishlist(
            @RequestHeader("userId") Long userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "6") int pageSize) {
        return wishlistService.getWishlist(userId, page, pageSize);
    }
}
