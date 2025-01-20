package sopt.jeolloga.domain.templestay.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.domain.templestay.api.dto.PageTemplestaySearchRes;
import sopt.jeolloga.domain.templestay.api.dto.TemplestayFilterReq;
import sopt.jeolloga.domain.templestay.api.dto.TemplestaySearchReq;
import sopt.jeolloga.domain.templestay.api.dto.TemplestaySearchRes;
import sopt.jeolloga.domain.templestay.api.service.TemplestaySearchService;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class TemplestaySearchController {
    private final TemplestaySearchService templestaySearchService;

    @PostMapping("/search")
    public ResponseEntity<PageTemplestaySearchRes<TemplestaySearchRes>> filterSearch(
            @RequestBody TemplestayFilterReq filterReq,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        Long userId = null; // 사용자 ID가 필요하면 RequestBody 또는 헤더에서 가져올 수 있습니다.

        // 필터 데이터를 2진수 비트 연산 값으로 변환
        Integer regionFilter = calculateBitValue(filterReq.region());
        Integer typeFilter = calculateBitValue(filterReq.type());
        Integer purposeFilter = calculateBitValue(filterReq.purpose());
        Integer activityFilter = calculateBitValue(filterReq.activity());
        Integer etcFilter = calculateBitValue(filterReq.etc());
        Integer minPrice = filterReq.price() != null ? filterReq.price().minPrice() : null;
        Integer maxPrice = filterReq.price() != null ? filterReq.price().maxPrice() : null;

        PageTemplestaySearchRes<TemplestaySearchRes> results = templestaySearchService.filterTemplestay(
                userId, "", page, pageSize, regionFilter, typeFilter, purposeFilter, activityFilter, etcFilter, minPrice, maxPrice);
        return ResponseEntity.ok(results);
    }

    private Integer calculateBitValue(Map<String, Integer> filter) {
        if (filter == null || filter.isEmpty()) {
            return null;
        }
        return filter.entrySet().stream()
                .filter(entry -> entry.getValue() == 1)
                .map(Map.Entry::getKey)
                .map(this::getBinaryValue)
                .reduce(0, (a, b) -> a | b);
    }

    private Integer getBinaryValue(String filterKey) {
        // 필터 키에 따라 2진수 값 반환
        return switch (filterKey) {
            case "강원" -> 0b000000000000001;
            case "경기" -> 0b000000000000010;
            case "경남" -> 0b000000000000100;
            case "경북" -> 0b000000000001000;
            case "광주" -> 0b000000000010000;
            case "대구" -> 0b000000000100000;
            case "대전" -> 0b000000001000000;
            case "부산" -> 0b000000010000000;
            case "서울" -> 0b000000100000000;
            case "인천" -> 0b000001000000000;
            case "전남" -> 0b000010000000000;
            case "전북" -> 0b000100000000000;
            case "제주" -> 0b001000000000000;
            case "충남" -> 0b010000000000000;
            case "충북" -> 0b100000000000000;
            case "당일형" -> 0b001;
            case "휴식형" -> 0b010;
            case "체험형" -> 0b100;
            case "힐링" -> 0b00000001;
            case "전통문화 체험" -> 0b00000010;
            case "심신치유" -> 0b00000100;
            case "자기계발" -> 0b00001000;
            case "여행 일정" -> 0b00010000;
            case "사찰순례" -> 0b00100000;
            case "휴식" -> 0b01000000;
            case "호기심" -> 0b10000000;
            case "발우공양" -> 0b0000000000001;
            case "108배" -> 0b0000000000010;
            case "스님과의 차담" -> 0b0000000000100;
            case "등산" -> 0b0000000001000;
            case "새벽 예불" -> 0b0000000010000;
            case "사찰 탐방" -> 0b0000000100000;
            case "염주 만들기" -> 0b0000001000000;
            case "연등 만들기" -> 0b0000010000000;
            case "다도" -> 0b0000100000000;
            case "명상" -> 0b0001000000000;
            case "산책" -> 0b0010000000000;
            case "요가" -> 0b0100000000000;
            case "기타" -> 0b1000000000000;
            case "절밥이 맛있는" -> 0b00000001;
            case "TV에 나온" -> 0b00000010;
            case "연예인이 다녀간" -> 0b00000100;
            case "근처 관광지가 많은" -> 0b00001000;
            case "속세와 멀어지고 싶은" -> 0b00010000;
            case "단체 가능" -> 0b00100000;
            case "동물 친구들과 함께" -> 0b01000000;
            case "유튜브 운영 중인" -> 0b10000000;
            default -> 0;
        };
    }

}
