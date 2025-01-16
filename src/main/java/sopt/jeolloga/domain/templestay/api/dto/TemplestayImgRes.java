package sopt.jeolloga.domain.templestay.api.dto;

import java.util.List;

public record TemplestayImgRes(
        int total,
        List<TemplestayImg> templestayImgs
) {
    public static record TemplestayImg(
            Long id,
            String imgUrl
    ) {
    }
}

