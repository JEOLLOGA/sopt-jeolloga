package sopt.jeolloga.domain.wishlist.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sopt.jeolloga.common.CategoryUtils;
import sopt.jeolloga.domain.member.Member;
import sopt.jeolloga.domain.member.MemberRepository;
import sopt.jeolloga.domain.templestay.core.*;
import sopt.jeolloga.domain.wishlist.api.dto.PageWishlistRes;
import sopt.jeolloga.domain.wishlist.api.dto.WishlistTemplestayRes;
import sopt.jeolloga.domain.wishlist.core.Wishlist;
import sopt.jeolloga.domain.wishlist.core.WishlistRepository;
import sopt.jeolloga.domain.wishlist.core.exception.WishlistCoreException;
import sopt.jeolloga.domain.wishlist.core.exception.WishlistNotFoundException;
import sopt.jeolloga.exception.ErrorCode;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final MemberRepository memberRepository;
    private final TemplestayRepository templestayRepository;
    private final CategoryRepository categoryRepository;
    private final UrlRepository urlRepository;

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

    @Transactional
    public PageWishlistRes<WishlistTemplestayRes> getWishlist(Long userId, int page, int pageSize) {

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new WishlistCoreException(ErrorCode.NOT_FOUND_TARGET));

        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Wishlist> wishlistPage = wishlistRepository.findAllByMember(member, pageable);

        List<WishlistTemplestayRes> wishlistResList = wishlistPage.stream()
                .map(w-> {
                    Templestay templestay=w.getTemplestay();

                    Category category = categoryRepository.findByTemplestayId(templestay.getId())
                            .orElseThrow(() -> new WishlistCoreException(ErrorCode.NOT_FOUND_TARGET));

                    String tag = templestay.getTag() != null && !templestay.getTag().isEmpty()
                            ? templestay.getTag().split(",")[0] : null;

                    String region = CategoryUtils.getRegionName(category.getRegion());
                    String type = CategoryUtils.getTypeName(category.getType());

                    String imgUrl = urlRepository.findImgUrlByTemplestayId(templestay.getId())
                            .orElseThrow(() -> new WishlistNotFoundException());

                    boolean liked = wishlistRepository.existsByMemberIdAndTemplestayId(userId, templestay.getId());

                    return new WishlistTemplestayRes(
                            templestay.getId(),
                            templestay.getTempleName(),
                            templestay.getOrganizedName(),
                            tag,
                            region,
                            type,
                            imgUrl,
                            liked
                    );
                }).toList();
        return new PageWishlistRes<>(
                page,
                pageSize,
                wishlistPage.getTotalPages(),
                wishlistResList
        );
    }
}
