package sopt.jeolloga.domain.templestay.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sopt.jeolloga.common.CategoryUtils;
import sopt.jeolloga.domain.templestay.api.dto.TemplestayRankingListRes;
import sopt.jeolloga.domain.templestay.api.dto.TemplestayRankingRes;
import sopt.jeolloga.domain.templestay.core.*;
import sopt.jeolloga.domain.wishlist.core.WishlistRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class TemplestayRankingService {
    private final TemplestayRepository templestayRepository;
    private final CategoryRepository categoryRepository;
    private final WishlistRepository wishlistRepository;
    private final UrlRepository urlRepository;
    private final TemplestayImageRepository templestayImageRepository;

    @Transactional
    public TemplestayRankingListRes getTopRankingList(Long userId) {
        List<Long> rankingIds = List.of(244L, 196L, 4L);

        List<TemplestayRankingRes> rankings = rankingIds.stream()
                .map(id -> {
                    var templestay = templestayRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Invalid Templestay ID: " + id));

                    String tag = templestay.getTag() != null && !templestay.getTag().isEmpty()
                            ? templestay.getTag().split(",")[0]
                            : null;

                    Category category = categoryRepository.findByTemplestayId(id)
                            .orElse(null);

                    String region = category != null ? CategoryUtils.getRegionName(category.getRegion()) : "알 수 없음";

                    String imgUrl = templestayImageRepository.findImgUrlByTemplestayId(id).orElse("이미지 없음");

                    boolean liked = userId != null && wishlistRepository.existsByMemberIdAndTemplestayId(userId, id);

                    return new TemplestayRankingRes(
                            rankingIds.indexOf(id) + 1,
                            templestay.getId(),
                            templestay.getTempleName(),
                            tag,
                            region,
                            liked,
                            imgUrl
                    );
                })
                .collect(Collectors.toList());

        return TemplestayRankingListRes.of(rankings);
    }
}
