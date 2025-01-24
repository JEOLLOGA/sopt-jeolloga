package sopt.jeolloga;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sopt.jeolloga.common.Filters;
import sopt.jeolloga.domain.templestay.core.Category;


import static org.junit.jupiter.api.Assertions.*;

class FiltersTest {

    private Filters filters;


    @BeforeEach
    void setUp() {
        filters = new Filters();
    }

    @Test
    void testMatchesFilter() {

        // Given
        Category testCategory = new Category();

        // 지역: 서울(000000100000000)
        testCategory.setRegion(0b000000100000000);

        // 유형: 체험형(100)
        testCategory.setType(0b100);

        // 목적: 심신치유(00000100), 자기계발(00001000), 휴식(01000000)
        testCategory.setPurpose(0b01001100);

        // 체험: 스님과의 차담(0000000000100), 등산(0000000001000), 염주 만들기(0000001000000)
        testCategory.setActivity(0b0000001001100);

        // 기타: 속세와 멀어지고 싶은(00010000), 단체 가능(00100000)
        testCategory.setEtc(0b00110000);

        // 가격: 15000
        testCategory.setPrice(15000);

        // 필터 값
        int binaryRegionFilter = 0b000000100000010; // 서울, 경기
        int binaryTypeFilter = 0b101;               // 당일형, 체험형
        int binaryPurposeFilter = 0b01000101;       // 힐링, 심신치유, 휴식
        int binaryActivityFilter = 0b0000000011110; // 108배, 스님과의 차담, 등산, 새벽 예불
        int binaryEtcFilter = 0b10010100;           // 유튜브 운영 중인, 속세와 멀어지고 싶은, 연예인이 다녀간
        int minPrice = 10000;   // 가격 필터 10,000원 ~ 20,000원
        int maxPrice = 20000;

        // When
        boolean result = filters.matchesFilter(
                testCategory,
                binaryRegionFilter,
                binaryTypeFilter,
                binaryPurposeFilter,
                binaryActivityFilter,
                binaryEtcFilter,
                minPrice,
                maxPrice
        );

        // Then
        assertTrue(result, "사용자의 필터 항목에 부합하는 템플스테이입니다");
    }

    @Test
    void testMatchesFilterPriceOutOfBounds() {

        // Given
        Category testCategory = new Category();

        // 지역: 서울(000000100000000)
        testCategory.setRegion(0b000000100000000);

        // 유형: 체험형(100)
        testCategory.setType(0b100);

        // 목적: 심신치유(00000100), 자기계발(00001000), 휴식(01000000)
        testCategory.setPurpose(0b01001100);

        // 체험: 스님과의 차담(0000000000100), 등산(0000000001000), 염주 만들기(0000001000000)
        testCategory.setActivity(0b0000001001100);

        // 기타: 속세와 멀어지고 싶은(00010000), 단체 가능(00100000)
        testCategory.setEtc(0b00110000);

        // 가격: 15000
        testCategory.setPrice(15000);

        // 필터 값
        int binaryRegionFilter = 0b000000100000010; // 서울, 경기
        int binaryTypeFilter = 0b101;               // 당일형, 체험형
        int binaryPurposeFilter = 0b01000101;       // 힐링, 심신치유, 휴식
        int binaryActivityFilter = 0b0000000011110; // 108배, 스님과의 차담, 등산, 새벽 예불
        int binaryEtcFilter = 0b10010100;           // 유튜브 운영 중인, 속세와 멀어지고 싶은, 연예인이 다녀간
        int minPrice = 20000;   // 가격 필터 20,000원 ~ 30,000원
        int maxPrice = 30000;

        // When
        boolean result = filters.matchesFilter(
                testCategory,
                binaryRegionFilter,
                binaryTypeFilter,
                binaryPurposeFilter,
                binaryActivityFilter,
                binaryEtcFilter,
                minPrice,
                maxPrice
        );

        // Then
        assertFalse(result, "사용자의 가격 조건에 부합하지 않습니다");
    }

    @Test
    void testMatchesFilterBinaryMismatch() {
        // Given
        Category testCategory = new Category();

        // 지역: 서울(000000100000000)
        testCategory.setRegion(0b000000100000000);

        // 유형: 체험형(100)
        testCategory.setType(0b100);

        // 목적: 심신치유(00000100), 자기계발(00001000), 휴식(01000000)
        testCategory.setPurpose(0b01001100);

        // 체험: 스님과의 차담(0000000000100), 등산(0000000001000), 염주 만들기(0000001000000)
        testCategory.setActivity(0b0000001001100);

        // 기타: 속세와 멀어지고 싶은(00010000), 단체 가능(00100000)
        testCategory.setEtc(0b00110000);

        // 가격: 15000
        testCategory.setPrice(15000);

        // 필터 값
        int binaryRegionFilter = 0b000000000000010; // 경기
        int binaryTypeFilter = 0b101;               // 당일형, 체험형
        int binaryPurposeFilter = 0b01000101;       // 힐링, 심신치유, 휴식
        int binaryActivityFilter = 0b0000000011110; // 108배, 스님과의 차담, 등산, 새벽 예불
        int binaryEtcFilter = 0b10010100;           // 유튜브 운영 중인, 속세와 멀어지고 싶은, 연예인이 다녀간
        int minPrice = 10000;   // 가격 필터 10,000원 ~ 20,000원
        int maxPrice = 20000;

        // When
        boolean result = filters.matchesFilter(
                testCategory,
                binaryRegionFilter,
                binaryTypeFilter,
                binaryPurposeFilter,
                binaryActivityFilter,
                binaryEtcFilter,
                minPrice,
                maxPrice
        );

        // Then
        assertFalse(result, "사용자의 필터 조건에 부합하지 않는 템플스테이입니다");
    }
}
