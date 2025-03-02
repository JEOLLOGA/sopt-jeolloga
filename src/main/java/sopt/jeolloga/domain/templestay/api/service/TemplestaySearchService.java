package sopt.jeolloga.domain.templestay.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.jeolloga.common.FilterUtil;
import sopt.jeolloga.domain.member.core.Member;
import sopt.jeolloga.domain.member.core.MemberRepository;
import sopt.jeolloga.domain.member.core.Search;
import sopt.jeolloga.domain.member.core.SearchRepository;
import sopt.jeolloga.domain.templestay.api.dto.FilterReq;
import sopt.jeolloga.domain.templestay.api.dto.PageTemplestaySearchRes;
import sopt.jeolloga.domain.templestay.api.dto.TemplestayRes;
import sopt.jeolloga.domain.templestay.core.TemplestayRepository;
import sopt.jeolloga.domain.templestay.core.exception.TemplestayCoreException;
import sopt.jeolloga.exception.ErrorCode;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TemplestaySearchService {

    private final TemplestayRepository templestayRepository;
    private final MemberRepository memberRepository;
    private final SearchRepository searchRepository;

    public TemplestaySearchService(TemplestayRepository templestayRepository, MemberRepository memberRepository,SearchRepository searchRepository){
        this.templestayRepository = templestayRepository;
        this.memberRepository = memberRepository;
        this.searchRepository = searchRepository;
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

    public PageTemplestaySearchRes<TemplestayRes> searchFilteredTemplestay(FilterReq filter, int page, int pageSize, Long userId){

        String content = (filter.content() == null || filter.content().isBlank()) ? "" : filter.content().replaceAll("\\s+", "").trim();
        Integer binaryRegionFilter = FilterUtil.convertRegion(filter.region());
        Integer binaryTypeFilter = FilterUtil.convertType(filter.type());
        Integer binaryPurposeFilter = FilterUtil.convertPurpose(filter.purpose());
        Integer binaryActivityFilter = FilterUtil.convertActivity(filter.activity());
        Integer binaryEtcFilter = FilterUtil.convertEtc(filter.etc());

        int minPrice = filter.price().minPrice();
        int maxPrice = (filter.price().maxPrice() >= 300000) ? Integer.MAX_VALUE : filter.price().maxPrice();

        if (userId != null) {
            saveSearchContent(userId, content);
        }

        Pageable pageable = PageRequest.of(page-1, pageSize);
        Page<Object[]> searchFilteredTemplestayPage = templestayRepository.searchFilteredTemplestay(content, binaryRegionFilter, binaryTypeFilter,
                binaryPurposeFilter,binaryActivityFilter, minPrice, maxPrice, binaryEtcFilter, userId, pageable);

        List<TemplestayRes> templestayList = searchFilteredTemplestayPage.getContent().stream()
                .map(row -> new TemplestayRes(
                        (Long) row[0],  // templestayId
                        (String) row[1],  // templeName
                        (String) row[2],  // templestayName
                        (String) row[3],  // tag
                        FilterUtil.convertRegionToString((Integer) row[4]),  // region
                        FilterUtil.convertTypeToString((Integer) row[5]),  // type
                        (String) row[6],  // imageUrl
                        ((Number) row[7]).intValue() == 1  // liked
                ))
                .collect(Collectors.toList());

        return new PageTemplestaySearchRes<>(page, pageSize, searchFilteredTemplestayPage.getTotalPages(), content, templestayList
        );
    }

}
