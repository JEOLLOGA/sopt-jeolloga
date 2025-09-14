package sopt.jeolloga.domain.templestay.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.jeolloga.domain.templestay.api.dto.TemplestayViewReq;
import sopt.jeolloga.domain.templestay.core.Templestay;
import sopt.jeolloga.domain.templestay.core.TemplestayRepository;
import sopt.jeolloga.domain.templestay.core.exception.TemplestayCoreException;
import sopt.jeolloga.exception.ErrorCode;

@RequiredArgsConstructor
@Service
public class TemplestayViewService {

    private final TemplestayRepository templestayRepository;

    @Transactional
    public void addView(TemplestayViewReq request) {

        Long templestayId = request.templestayId();

        Templestay templestay = templestayRepository.findById(templestayId)
                .orElseThrow(() -> new TemplestayCoreException(ErrorCode.NOT_FOUND_TEMPLESTAY));

        templestay.setView(templestay.getView() + 1);
    }

}
