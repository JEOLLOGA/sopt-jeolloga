package sopt.jeolloga.domain.templestay.api.dto;

import java.math.BigDecimal;

public record TemplestayDetailRes(
        Long id,
        String templeName,
        String templestayName,
        String address,
        String phoneNumber,
        String tag,
        String templestayPrice,
        String introduction,
        String detailAddress,
        String schedule,
        BigDecimal latitude,
        BigDecimal longtitude,
        boolean liked,
        String url
){
}
