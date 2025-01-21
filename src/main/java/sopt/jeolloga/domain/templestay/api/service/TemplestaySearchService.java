package sopt.jeolloga.domain.templestay.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sopt.jeolloga.common.CategoryUtils;
import sopt.jeolloga.domain.member.core.Member;
import sopt.jeolloga.domain.member.core.MemberRepository;
import sopt.jeolloga.domain.member.core.Search;
import sopt.jeolloga.domain.member.core.SearchRepository;
import sopt.jeolloga.domain.templestay.api.dto.PageTemplestaySearchRes;
import sopt.jeolloga.domain.templestay.api.dto.TemplestaySearchRes;
import sopt.jeolloga.domain.templestay.core.*;
import sopt.jeolloga.domain.templestay.core.exception.TemplestayCoreException;
import sopt.jeolloga.domain.wishlist.core.WishlistRepository;
import sopt.jeolloga.exception.ErrorCode;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class TemplestaySearchService {
    private final TemplestayRepository templestayRepository;
    private final SearchRepository searchRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final UrlRepository urlRepository;
    private final WishlistRepository wishlistRepository;
    private final TemplestayImageRepository templestayImageRepository;

    @Transactional
    public PageTemplestaySearchRes<TemplestaySearchRes> searchTemplestay(Long userId, String query, int page, int pageSize) {
        String sanitizedQuery = query.replaceAll("\\s+", "");

        saveSearchContent(userId, query);

        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Object[]> results = templestayRepository.searchByTempleNameWithPagination(sanitizedQuery, pageable);

        if (results.isEmpty()) {
            return new PageTemplestaySearchRes<>(page, pageSize, 0, List.of());
        }

        List<TemplestaySearchRes> templestaySearchResults = results.stream()
                .map(result -> {
                    Long id = ((Number) result[0]).longValue();
                    String templeName = (String) result[1];
                    String organizedName = result.length > 2 && result[2] != null ? (String) result[2] : null;
                    String tag = result.length > 3 && result[3] != null ? ((String) result[3]).split(",")[0] : null;

                    String region = null;
                    String type = null;
                    String imgUrl = null;
                    boolean liked = false;

                    if (id != null) {
                        Category category = categoryRepository.findByTemplestayId(id)
                                .orElseThrow(() -> new TemplestayCoreException(ErrorCode.NOT_FOUND_TARGET));
                        region = CategoryUtils.getRegionName(category.getRegion());
                        type = CategoryUtils.getTypeName(category.getType());
                        imgUrl = templestayImageRepository.findImgUrlByTemplestayId(id).orElse(null);
                        if (userId != null) {
                            liked = wishlistRepository.existsByMemberIdAndTemplestayId(userId, id);
                        }
                    }

                    return new TemplestaySearchRes(
                            id,
                            templeName,
                            organizedName,
                            tag,
                            region,
                            type,
                            imgUrl,
                            liked
                    );
                })
                .collect(Collectors.toList());

        return new PageTemplestaySearchRes<>(page, pageSize, results.getTotalPages(), templestaySearchResults);
    }

    @Transactional
    private void saveSearchContent(Long userId, String content) {
        if (userId == null || content == null || content.isBlank()) {
            return;
        }

        Member member = null;
        if (userId != null) {
            member = memberRepository.findById(userId)
                    .orElseThrow(() -> new TemplestayCoreException(ErrorCode.NOT_FOUND_USER));
        }

        Search search = new Search(member, content);
        searchRepository.save(search);
    }

}