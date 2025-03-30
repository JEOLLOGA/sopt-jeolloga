package sopt.jeolloga.domain.member.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sopt.jeolloga.domain.member.api.dto.RecentViewReq;
import sopt.jeolloga.domain.member.api.dto.RecentViewRes;
import sopt.jeolloga.domain.member.core.*;
import sopt.jeolloga.domain.member.core.exception.MemberCoreException;
import sopt.jeolloga.domain.templestay.api.dto.TemplestayRes;
import sopt.jeolloga.domain.templestay.core.Templestay;
import sopt.jeolloga.domain.templestay.core.TemplestayRepository;
import sopt.jeolloga.exception.ErrorCode;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RecentViewService {

    private final RecentViewRepository recentViewRepository;
    private final MemberRepository memberRepository;
    private final TemplestayRepository templestayRepository;

    public void saveRecentView(RecentViewReq request) {

        Long memberId = request.memberId();
        Long templestayId = request.templestayId();

        if (memberId == null) throw new MemberCoreException(ErrorCode.MISSING_USER_ID);
        if (templestayId == null) throw new MemberCoreException(ErrorCode.MISSING_TEMPLESTAY_ID);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberCoreException(ErrorCode.NOT_FOUND_USER));
        Templestay templestay = templestayRepository.findById(templestayId)
                .orElseThrow(() -> new MemberCoreException(ErrorCode.NOT_FOUND_TEMPLESTAY));

        recentViewRepository.findByMemberOrderByIdAsc(member).stream()
                .filter(rv -> rv.getTemplestay().getId().equals(templestayId))
                .findFirst()
                .ifPresent(recentViewRepository::delete);

        List<RecentView> recentViews = recentViewRepository.findByMemberOrderByIdAsc(member);
        if (recentViews.size() >= 10) {
            recentViewRepository.delete(recentViews.get(0));
        }

        recentViewRepository.save(new RecentView(member, templestay));
    }


    public RecentViewRes getRecentViewedTemplestays(Long memberId) {

        if (memberId == null) throw new MemberCoreException(ErrorCode.MISSING_USER_ID);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberCoreException(ErrorCode.NOT_FOUND_USER));

        return new RecentViewRes(
                recentViewRepository.findRecentViewedTemplestays(memberId).stream()
                        .map(row -> new TemplestayRes(
                                ((Number) row[0]).longValue(),    // templestayId
                                (String) row[1],                   // templeName
                                (String) row[2],                   // templestayName
                                (String) row[3],                   // tag
                                String.valueOf(row[4]),            // region
                                String.valueOf(row[5]),            // type
                                (String) row[6],                   // imageUrl
                                ((Number) row[7]).intValue() == 1     // liked
                        ))
                        .toList()
        );
    }
}
