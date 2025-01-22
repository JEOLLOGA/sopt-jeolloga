package sopt.jeolloga.domain.templestay.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import sopt.jeolloga.common.DataUtils;
import sopt.jeolloga.domain.templestay.api.dto.PageReviewRes;
import sopt.jeolloga.domain.templestay.api.dto.ReviewRes;
import sopt.jeolloga.domain.templestay.core.Review;
import sopt.jeolloga.domain.templestay.core.ReviewRepository;
import sopt.jeolloga.domain.templestay.core.TemplestayRepository;
import sopt.jeolloga.domain.templestay.core.exception.TemplestayCoreException;
import sopt.jeolloga.domain.templestay.core.exception.TemplestayNotFoundException;
import sopt.jeolloga.exception.ErrorCode;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TemplestayRepository templestayRepository;

    public PageReviewRes<ReviewRes> getPagedReviewsByTemplestayId(Long templestayId, int page, int size) {

        String templeName = templestayRepository.findById(templestayId)
                .orElseThrow(TemplestayNotFoundException::new)
                .getTempleName();

        PageRequest pageRequest = PageRequest.of(page-1,size);

        Page<Review> reviews = reviewRepository.findByTempleNameOrderByReviewDateDesc(templeName, pageRequest);

        if (reviews.isEmpty()) {
            throw new TemplestayCoreException(ErrorCode.REVIEW_NOT_FOUND);
        }

        Long count = reviewRepository.countByTempleName(templeName);

        List<ReviewRes> reviewDtos = reviews.getContent().stream()
                .map(review -> new ReviewRes(
                        review.getId(),
                        review.getReviewTitle(),
                        review.getReviewLink(),
                        review.getReviewName(),
                        review.getReviewDescription(),
                        DataUtils.formatReviewDate(review.getReviewDate()),
                        review.getReviewImgUrl()
                ))
                .toList();

        return new PageReviewRes<>(
                templestayId,
                count,
                page,
                size,
                reviews.getTotalPages(),
                reviewDtos
        );
    }
}
