package sopt.jeolloga.domain.templestay.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sopt.jeolloga.domain.templestay.api.dto.TemplestayImgRes;
import sopt.jeolloga.domain.templestay.api.dto.TemplestayImgRes.TemplestayImg;
import sopt.jeolloga.domain.templestay.core.TemplestayImage;
import sopt.jeolloga.domain.templestay.core.TemplestayImageRepository;
import sopt.jeolloga.domain.templestay.core.exception.TemplestayCoreException;
import sopt.jeolloga.exception.ErrorCode;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class TemplestayImageService {
    private final TemplestayImageRepository templestayImageRepository;

    @Transactional(readOnly = true)
    public TemplestayImgRes getTemplestayImages(Long templestayId) {
        List<TemplestayImage> images = templestayImageRepository.findAllByTemplestayId(templestayId);

        if (images.isEmpty()) {
            throw new TemplestayCoreException(ErrorCode.NOT_FOUND_TARGET);
        }

        List<TemplestayImg> templestayImgs = images.stream()
                .map(img -> new TemplestayImg(img.getId(), img.getImgUrl()))
                .collect(Collectors.toList());

        return new TemplestayImgRes(templestayImgs.size(), templestayImgs);
    }
}
