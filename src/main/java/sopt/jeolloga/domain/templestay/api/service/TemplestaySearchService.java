package sopt.jeolloga.domain.templestay.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.jeolloga.common.CategoryUtils;
import sopt.jeolloga.domain.member.core.Member;
import sopt.jeolloga.domain.member.core.MemberRepository;
import sopt.jeolloga.domain.member.core.Search;
import sopt.jeolloga.domain.member.core.SearchRepository;
import sopt.jeolloga.domain.templestay.api.dto.PageTemplestaySearchRes;
import sopt.jeolloga.domain.templestay.api.dto.TemplestaySearchRes;
import sopt.jeolloga.domain.templestay.core.TemplestayRepository;
import sopt.jeolloga.domain.templestay.core.exception.TemplestayCoreException;
import sopt.jeolloga.domain.wishlist.core.WishlistRepository;
import sopt.jeolloga.exception.ErrorCode;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ch.qos.logback.core.joran.JoranConstants.NULL;

@RequiredArgsConstructor
@Service
public class TemplestaySearchService {
    private final TemplestayRepository templestayRepository;
    private final WishlistRepository wishlistRepository;
    private final MemberRepository memberRepository;
    private final SearchRepository searchRepository;

    @Transactional
    public PageTemplestaySearchRes<TemplestaySearchRes> searchTemplestayWithFilters(
            Long userId,
            String query,
            Map<String, Integer> region,
            Map<String, Integer> type,
            Map<String, Integer> purpose,
            Map<String, Integer> activity,
            Map<String, Integer> etc,
            Pageable pageable
    ) {
        if (userId != null) {
            saveSearchContent(userId, query);
        }

        String sanitizedQuery = (query == null || query.isBlank()) ? "" : query.trim();

        Integer regionFilter = calculateBitValue(region, "region");
        Integer typeFilter = calculateBitValue(type, "type");
        Integer purposeFilter = calculateBitValue(purpose, "purpose");
        Integer activityFilter = calculateBitValue(activity, "activity");
        Integer etcFilter = calculateBitValue(etc, "etc");

        List<Object[]> searchResults = templestayRepository.searchWithFiltersAndData(
                sanitizedQuery, regionFilter, typeFilter, purposeFilter, activityFilter, etcFilter);

        if (searchResults.isEmpty()) {
            return new PageTemplestaySearchRes<>(
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize(),
                    0,
                    sanitizedQuery,
                    List.of()
            );
        }

        int totalPages = (int) Math.ceil((double) searchResults.size() / pageable.getPageSize());

        List<Object[]> paginatedResults = searchResults.stream()
                .skip((long) pageable.getPageNumber() * pageable.getPageSize())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());

        List<TemplestaySearchRes> templestaySearchResults = paginatedResults.stream()
                .map(result -> {
                    Long id = ((Number) result[0]).longValue();
                    String templeName = result[1] != null ? result[1].toString() : null;
                    String organizedName = result[2] != null ? result[2].toString() : null;
                    String tag = result[3] != null ? result[3].toString().split(",")[0] : null;
                    String regionName = result.length > 4 && result[4] != null
                            ? CategoryUtils.getRegionName((Integer) result[4])
                            : null;
                    String typeName = result.length > 5 && result[5] != null
                            ? CategoryUtils.getTypeName((Integer) result[5])
                            : null;
                    String imgUrl = result.length > 9 && result[9] != null
                            ? result[9].toString() : null;
                    boolean liked = (userId != null) && wishlistRepository.existsByMemberIdAndTemplestayId(userId, id);

                    return new TemplestaySearchRes(
                            id,
                            templeName,
                            organizedName,
                            tag,
                            regionName,
                            typeName,
                            imgUrl,
                            liked
                    );
                })
                .collect(Collectors.toList());

        return new PageTemplestaySearchRes<>(
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                totalPages,
                sanitizedQuery,
                templestaySearchResults
        );
    }

    private Integer calculateBitValue(Map<String, Integer> filter, String categoryType) {
        if (filter == null || filter.isEmpty() || filter.values().stream().allMatch(value -> value == 0)) {
            return switch (categoryType) {
                case "region" -> 0b111111111111111;
                case "type" -> 0b111;
                case "purpose" -> 0b11111111;
                case "activity" -> 0b1111111111111;
                case "etc" -> 0b11111111;
                default -> 0;
            };
        }
        return filter.entrySet().stream()
                .filter(entry -> entry.getValue() == 1)
                .map(Map.Entry::getKey)
                .map(this::getBinaryValue)
                .reduce(0, (a, b) -> a | b);
    }

    @Transactional
    private void saveSearchContent(Long userId, String content) {
        if (content == null || content.isBlank()) {
            content = "";
        }

        Member member = null;
        if (userId != null) {
            member = memberRepository.findById(userId)
                    .orElseThrow(() -> new TemplestayCoreException(ErrorCode.NOT_FOUND_USER));
        } else {
            throw new TemplestayCoreException(ErrorCode.MISSING_USER_ID);
        }

        try {
            Search search = new Search(member, content);
            searchRepository.save(search);
        } catch (Exception e) {
            throw new TemplestayCoreException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    private Integer getBinaryValue(String filterKey) {
        return switch (filterKey) {
            case "강원" -> 0b000000000000001;
            case "경기" -> 0b000000000000010;
            case "경남" -> 0b000000000000100;
            case "경북" -> 0b000000000001000;
            case "광주" -> 0b000000000010000;
            case "대구" -> 0b000000000100000;
            case "대전" -> 0b000000001000000;
            case "부산" -> 0b000000010000000;
            case "서울" -> 0b000000100000000;
            case "인천" -> 0b000001000000000;
            case "전남" -> 0b000010000000000;
            case "전북" -> 0b000100000000000;
            case "제주" -> 0b001000000000000;
            case "충남" -> 0b010000000000000;
            case "충북" -> 0b100000000000000;
            case "당일형" -> 0b001;
            case "휴식형" -> 0b010;
            case "체험형" -> 0b100;
            case "힐링" -> 0b00000001;
            case "전통문화 체험" -> 0b00000010;
            case "심신치유" -> 0b00000100;
            case "자기계발" -> 0b00001000;
            case "여행 일정" -> 0b00010000;
            case "사찰순례" -> 0b00100000;
            case "휴식" -> 0b01000000;
            case "호기심" -> 0b10000000;
            case "발우공양" -> 0b0000000000001;
            case "108배" -> 0b0000000000010;
            case "스님과의 차담" -> 0b0000000000100;
            case "등산" -> 0b0000000001000;
            case "새벽 예불" -> 0b0000000010000;
            case "사찰 탐방" -> 0b0000000100000;
            case "염주 만들기" -> 0b0000001000000;
            case "연등 만들기" -> 0b0000010000000;
            case "다도" -> 0b0000100000000;
            case "명상" -> 0b0001000000000;
            case "산책" -> 0b0010000000000;
            case "요가" -> 0b0100000000000;
            case "기타" -> 0b1000000000000;
            case "절밥이 맛있는" -> 0b00000001;
            case "TV에 나온" -> 0b00000010;
            case "연예인이 다녀간" -> 0b00000100;
            case "근처 관광지가 많은" -> 0b00001000;
            case "속세와 멀어지고 싶은" -> 0b00010000;
            case "단체 가능" -> 0b00100000;
            case "동물 친구들과 함께" -> 0b01000000;
            case "유튜브 운영 중인" -> 0b10000000;
            default -> 0;
        };
    }
}

