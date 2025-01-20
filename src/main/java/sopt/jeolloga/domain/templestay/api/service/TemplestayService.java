package sopt.jeolloga.domain.templestay.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sopt.jeolloga.common.DataUtils;
import sopt.jeolloga.domain.templestay.api.dto.TemplestayDetailRes;
import sopt.jeolloga.domain.templestay.core.*;
import sopt.jeolloga.domain.templestay.core.exception.TemplestayCoreException;
import sopt.jeolloga.domain.wishlist.core.WishlistRepository;
import sopt.jeolloga.exception.ErrorCode;

@RequiredArgsConstructor
@Component
public class TemplestayService {
    private final TemplestayRepository templestayRepository;
    private final WishlistRepository wishlistRepository;
    private final UrlRepository urlRepository;
    private final CategoryRepository categoryRepository;
    @Transactional(readOnly = true)
    public TemplestayDetailRes getTemplestayDetails(Long userId, Long templestayId) {
        Templestay templestay = templestayRepository.findById(templestayId)
                .orElseThrow(() -> new TemplestayCoreException(ErrorCode.NOT_FOUND_TARGET));

        boolean liked = false;
        if (userId != null) {
            liked = wishlistRepository.findByMemberIdAndTemplestayId(userId, templestayId).isPresent();
        }

        String url = urlRepository.findByTemplestayId(templestayId)
                .map(Url::getTemplestayUrl)
                .orElse(null);

        String price = categoryRepository.findByTemplestayId(templestayId)
                .map(Category::getPrice)
                .map(DataUtils::convertPriceToString)
                .orElse(null);

        String fullAddress = templestay.getAddress();
        String address = DataUtils.extractFirstTwoWords(fullAddress);

        return new TemplestayDetailRes(
                templestay.getId(),
                templestay.getTempleName(),
                templestay.getOrganizedName(),
                address,
                templestay.getPhoneNumber(),
                templestay.getTag(),
                price,
                templestay.getIntroduction(),
                templestay.getAddress(),
                templestay.getSchedule(),
                templestay.getLatitude(),
                templestay.getLongitude(),
                liked,
                url
        );
    }
}

