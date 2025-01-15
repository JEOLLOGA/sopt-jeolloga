package sopt.jeolloga.domain.wishlist.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sopt.jeolloga.domain.member.Member;
import sopt.jeolloga.domain.member.MemberRepository;
import sopt.jeolloga.domain.templestay.core.Templestay;
import sopt.jeolloga.domain.templestay.core.TemplestayRepository;
import sopt.jeolloga.domain.wishlist.core.Wishlist;
import sopt.jeolloga.domain.wishlist.core.WishlistRepository;
import sopt.jeolloga.domain.wishlist.core.exception.WishlistCoreException;
import sopt.jeolloga.exception.ErrorCode;

import java.util.List;

@RequiredArgsConstructor
@Component
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final MemberRepository memberRepository;
    private final TemplestayRepository templestayRepository;

    @Transactional
    public void addWishlist(Long userId, Long templestayId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new WishlistCoreException(ErrorCode.NOT_FOUND_TARGET));

        Templestay templestay = templestayRepository.findById(templestayId)
                .orElseThrow(() -> new WishlistCoreException(ErrorCode.NOT_FOUND_TARGET));

        boolean exists = wishlistRepository.findByMemberAndTemplestay(member, templestay).isPresent();
        if (exists) {
            throw new WishlistCoreException(ErrorCode.DUPLICATE_WISHLIST);
        }
        Wishlist wishlist = new Wishlist(member, templestay);
        wishlistRepository.save(wishlist);
    }

    @Transactional
    public void deleteWishlist(Long userId, Long templestayId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new WishlistCoreException(ErrorCode.NOT_FOUND_TARGET));

        Templestay templestay = templestayRepository.findById(templestayId)
                .orElseThrow(() -> new WishlistCoreException(ErrorCode.NOT_FOUND_TARGET));

        Wishlist wishlist = wishlistRepository.findByMemberAndTemplestay(member, templestay)
                .orElseThrow(() -> new WishlistCoreException(ErrorCode.NOT_FOUND_TEMPLESTAY));

        wishlistRepository.delete(wishlist);
    }
}
