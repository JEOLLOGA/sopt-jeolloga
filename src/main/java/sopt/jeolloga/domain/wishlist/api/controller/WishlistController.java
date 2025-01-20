package sopt.jeolloga.domain.wishlist.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.common.ResponseDto;
import sopt.jeolloga.domain.wishlist.api.dto.PageWishlistRes;
import sopt.jeolloga.domain.wishlist.api.dto.WishlistReq;
import sopt.jeolloga.domain.wishlist.api.dto.WishlistTemplestayListRes;
import sopt.jeolloga.domain.wishlist.api.dto.WishlistTemplestayRes;
import sopt.jeolloga.domain.wishlist.api.service.WishlistService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WishlistController {
    private final WishlistService wishlistService;

    @PostMapping("/user/templestay/liked")
    public ResponseEntity<ResponseDto<?>> addWishlist(
            @RequestBody WishlistReq wishlistReq) {
        wishlistService.addWishlist(wishlistReq.userId(), wishlistReq.templestayId());
        return ResponseEntity.ok(ResponseDto.success("success"));
    }

    @DeleteMapping("/user/templestay/liked/delete")
    public ResponseEntity<ResponseDto<?>> deleteWishlist(
            @RequestBody WishlistReq wishlistReq) {
        wishlistService.deleteWishlist(wishlistReq.userId(), wishlistReq.templestayId());
        return ResponseEntity.ok(ResponseDto.success("success"));
    }

    @GetMapping("/user/wishlist")
    public PageWishlistRes<WishlistTemplestayRes> getWishlist(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "6") int pageSize) {
        return wishlistService.getWishlist(userId, page, pageSize);
    }
}
