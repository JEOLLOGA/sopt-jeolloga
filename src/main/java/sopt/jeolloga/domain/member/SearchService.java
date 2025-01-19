package sopt.jeolloga.domain.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sopt.jeolloga.domain.member.core.Member;
import sopt.jeolloga.domain.member.core.MemberRepository;
import sopt.jeolloga.domain.templestay.core.exception.TemplestayCoreException;
import sopt.jeolloga.exception.ErrorCode;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class SearchService {
    private final SearchRepository searchRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public SearchListRes getSearchHistory(Long userId) {
        if (userId == null) {
            throw new TemplestayCoreException(ErrorCode.MISSING_USER_ID);
        }

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new TemplestayCoreException(ErrorCode.NOT_FOUND_USER));

        List<Object[]> searchRecords = searchRepository.findTop10ByMemberIdOrderByIdDesc(userId);

        List<SearchRes> searchList = searchRecords.stream()
                .map(record -> new SearchRes(
                        ((Number) record[0]).longValue(),
                        (String) record[1]
                ))
                .collect(Collectors.toList());

        return new SearchListRes(searchList);
    }

    @Transactional
    public void deleteSearchRecord(Long userId, Long searchId) {
        if (userId == null) {
            throw new TemplestayCoreException(ErrorCode.MISSING_USER_ID);
        }

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new TemplestayCoreException(ErrorCode.NOT_FOUND_TARGET));

        Search search = searchRepository.findById(searchId)
                .orElseThrow(() -> new TemplestayCoreException(ErrorCode.NOT_FOUND_TARGET));


        if (!search.getMember().getId().equals(userId)) {
            throw new TemplestayCoreException(ErrorCode.NOT_FOUND_USER);
        }

        searchRepository.delete(search);
    }

    @Transactional
    public void deleteAllSearchRecords(Long userId) {
        if (userId == null) {
            throw new TemplestayCoreException(ErrorCode.NOT_FOUND_TARGET);
        }

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new TemplestayCoreException(ErrorCode.NOT_FOUND_USER));
        searchRepository.deleteAllByMemberId(userId);
    }
}
