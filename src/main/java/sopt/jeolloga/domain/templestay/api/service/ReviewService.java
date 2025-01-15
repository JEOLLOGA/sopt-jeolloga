package sopt.jeolloga.domain.templestay.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sopt.jeolloga.domain.templestay.api.dto.ReviewRes;
import sopt.jeolloga.domain.templestay.core.Review;
import sopt.jeolloga.domain.templestay.core.ReviewRepository;
import sopt.jeolloga.domain.templestay.core.Templestay;
import sopt.jeolloga.domain.templestay.core.TemplestayRepository;
import sopt.jeolloga.domain.templestay.core.exception.TemplestayCoreException;
import sopt.jeolloga.exception.ErrorCode;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TemplestayRepository templestayRepository;

    public List<ReviewRes> getReviewsByTemplestayId(Long templestayId) {
        Templestay templestay = templestayRepository.findById(templestayId)
                .orElseThrow(() -> new TemplestayCoreException(ErrorCode.NOT_FOUND_TEMPLESTAY));

        String templeName = templestay.getTempleName();

        List<Review> reviews = reviewRepository.findByTempleName(templeName);

        if (reviews.isEmpty()) {
            throw new TemplestayCoreException(ErrorCode.REVIEW_NOT_FOUND);
        }

        return reviews.stream()
                .map(review -> new ReviewRes(
                        review.getId(),
                        review.getReviewTitle(),
                        review.getReviewLink(),
                        review.getReviewName(),
                        review.getReviewDescription(),
                        review.getReviewDate(),
                        review.getReviewImgUrl()
                ))
                .collect(Collectors.toList());
    }

}
