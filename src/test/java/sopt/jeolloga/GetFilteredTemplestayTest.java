package sopt.jeolloga;

import org.junit.jupiter.api.Test; // JUnit 테스트 메서드
import org.junit.jupiter.api.extension.ExtendWith; // Mockito 확장을 사용하기 위한 어노테이션

import org.mockito.InjectMocks; // 테스트 클래스에 Mock 객체를 주입
import org.mockito.Mock; // Mock 객체를 생성
import org.mockito.junit.jupiter.MockitoExtension; // Mockito 확장

import static org.mockito.Mockito.*; // Mockito 메서드 (when, verify 등)
import static org.junit.jupiter.api.Assertions.*; // JUnit assertion 메서드 (assertEquals, assertTrue 등)

import org.springframework.data.domain.Page; // 페이지네이션 인터페이스
import org.springframework.data.domain.PageImpl; // 페이지네이션 구현체
import org.springframework.data.domain.PageRequest; // 페이지네이션 요청 객체
import org.springframework.data.domain.Pageable; // 페이지네이션 요청 인터페이스
import sopt.jeolloga.common.Filters;
import sopt.jeolloga.domain.templestay.api.dto.PageTemplestayRes;
import sopt.jeolloga.domain.templestay.api.dto.TemplestayRes;
import sopt.jeolloga.domain.templestay.api.dto.TemplestaySearchRes;
import sopt.jeolloga.domain.templestay.api.service.FilterService;
import sopt.jeolloga.domain.templestay.api.service.FilterServiceV1;
import sopt.jeolloga.domain.templestay.api.service.TemplestayService;
import sopt.jeolloga.domain.templestay.core.TemplestayRepository;
import sopt.jeolloga.domain.wishlist.core.WishlistRepository;

import java.util.List; // List 인터페이스
import java.util.Optional; // Optional 클래스

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TemplestayServiceTest {

    @Mock
    private TemplestayRepository templestayRepository;

    @Mock
    private WishlistRepository wishlistRepository;

    @InjectMocks
    private FilterServiceV1 filterServiceV1;

    @Mock
    private Filters filters;

    @Test
    void testGetFilteredTemplestay() {
        // Given
        List<Long> ids = List.of(1L, 2L, 3L);
        int page = 1;
        int size = 5;
        Long userId = 1L;

        // Mocking repository results
        List<Object[]> mockResults = List.of(
                new Object[]{
                        1L,                  // templestay_id
                        "선명상",             // temple_name
                        "선명상",             // organized_name
                        "여행 일정으로 딱 좋은, 명상, 주차 가능", // tag
                        0b000000100000000L,  // region
                        0b100L,              // type
                        "img1"               // img_url
                },
                new Object[]{
                        2L,                  // templestay_id
                        "내 마음 깊이 살펴보기 [선(禪)-명상체험형] [수향당]", // temple_name
                        "내 마음 깊이 살펴보기",                     // organized_name
                        "마음 챙김 명상, 새벽별 여행, 전통 불교 체험",    // tag
                        0b000000000100000L,  // region
                        0b010L,              // type
                        "img2"               // img_url
                }
        );
        Page<Object[]> mockPage = new PageImpl<>(mockResults, PageRequest.of(page - 1, size), mockResults.size());

        // Mock repository methods
        when(templestayRepository.findTemplestayWithDetails(ids, PageRequest.of(page - 1, size))).thenReturn(mockPage);
        when(wishlistRepository.existsByMemberIdAndTemplestayId(userId, 1L)).thenReturn(true);
        when(wishlistRepository.existsByMemberIdAndTemplestayId(userId, 2L)).thenReturn(false);

        // Mock Filters
        when(filters.getFilterKey(0b000000100000000, filters.getRegionFilter())).thenReturn("서울");
        when(filters.getFilterKey(0b000000000100000, filters.getRegionFilter())).thenReturn("대구");
        when(filters.getFilterKey(0b100, filters.getTypeFilter())).thenReturn("체험형");
        when(filters.getFilterKey(0b010, filters.getTypeFilter())).thenReturn("휴식형");

        // When
        PageTemplestayRes result = filterServiceV1.getFilteredTemplestay(ids, page, size, userId);

        // Then
        assertEquals(1, result.Page());
        assertEquals(5, result.pageSize());
        assertEquals(1, result.totalPages());
        assertEquals(2, result.templestays().size());

        // 첫 번째 템플스테이 데이터 검증
        TemplestayRes templestay1 = result.templestays().get(0);
        assertEquals(1L, templestay1.templestayId());
        assertEquals("선명상", templestay1.templeName());
        assertEquals("선명상", templestay1.templestayName());
        assertEquals("여행 일정으로 딱 좋은", templestay1.tag());
        assertEquals("서울", templestay1.region());
        assertEquals("체험형", templestay1.type());
        assertEquals("img1", templestay1.imgUrl());
        assertTrue(templestay1.liked());

        // 두 번째 템플스테이 데이터 검증
        TemplestayRes templestay2 = result.templestays().get(1);
        assertEquals(2L, templestay2.templestayId());
        assertEquals("내 마음 깊이 살펴보기 [선(禪)-명상체험형] [수향당]", templestay2.templeName());
        assertEquals("내 마음 깊이 살펴보기", templestay2.templestayName());
        assertEquals("마음 챙김 명상", templestay2.tag());
        assertEquals("대구", templestay2.region());
        assertEquals("휴식형", templestay2.type());
        assertEquals("img2", templestay2.imgUrl());
        assertFalse(templestay2.liked());
    }


}
