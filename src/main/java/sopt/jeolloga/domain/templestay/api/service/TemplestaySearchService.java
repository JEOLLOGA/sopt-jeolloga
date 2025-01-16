package sopt.jeolloga.domain.templestay.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sopt.jeolloga.domain.member.Member;
import sopt.jeolloga.domain.member.MemberRepository;
import sopt.jeolloga.domain.member.Search;
import sopt.jeolloga.domain.member.SearchRepository;
import sopt.jeolloga.domain.templestay.api.dto.TemplestaySearchRes;
import sopt.jeolloga.domain.templestay.core.TemplestayRepository;
import sopt.jeolloga.domain.templestay.core.exception.TemplestayCoreException;
import sopt.jeolloga.exception.ErrorCode;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class TemplestaySearchService {
    private final TemplestayRepository templestayRepository;
    private final SearchRepository searchRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public List<TemplestaySearchRes> searchTemplestay(Long userId, String query) {
        String sanitizedQuery = query.replaceAll("\\s+", "");

        if (userId != null) {
            saveSearchContent(userId, query);
        }

        List<Object[]> results = templestayRepository.searchByTempleName(sanitizedQuery);

        if (results.isEmpty()) {
            return List.of();
        }

        return results.stream()
                .map(result -> new TemplestaySearchRes(
                        ((Number) result[0]).longValue(),
                        (String) result[1]
                )).collect(Collectors.toList());
    }

    @Transactional
    private void saveSearchContent(Long userId, String content) {
        if (userId == null || content == null || content.isBlank()) {
            return;
        }

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new TemplestayCoreException(ErrorCode.NOT_FOUND_USER));

        Search search = new Search(member, content);
        searchRepository.save(search);
    }
}
