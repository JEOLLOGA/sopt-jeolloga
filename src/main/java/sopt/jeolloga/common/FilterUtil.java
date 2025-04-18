package sopt.jeolloga.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Component
@Slf4j
public class FilterUtil {

    private static Map<String, Map<String, Integer>> categoryBitmask;

    // JSON 파일 로드
    static {
        try {
            // ObjectMapper 생성
            ObjectMapper objectMapper = new ObjectMapper();

            // 클래스패스에서 Categories.json 파일을 읽어옴
            InputStream inputStream = FilterUtil.class.getClassLoader().getResourceAsStream("common/Categories.json");

            if (inputStream == null) {
                throw new RuntimeException("Categories.json 파일을 찾을 수 없습니다.");
            }

            log.info("현재 작업 디렉토리 >>>>>>>>>>>>>>>>>>>>>> " + System.getProperty("user.dir"));

            // JSON 파일을 Map으로 변환
            categoryBitmask = objectMapper.readValue(inputStream, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load category bitmask configuration", e);
        }
    }

    private static Integer convertToBitmask(Map<String, Integer> filterMap, String category) {
        Map<String, Integer> bitmaskMapping = categoryBitmask.get(category);
        if (bitmaskMapping == null) {
            throw new IllegalArgumentException("Invalid category: " + category);
        }

        return filterMap.entrySet().stream()
                .filter(entry -> entry.getValue() == 1)  // 1인 값만 처리
                .map(entry -> bitmaskMapping.getOrDefault(entry.getKey(), 0))
                .reduce(0, (a, b) -> a | b);
    }

    public static Integer convertRegion(Map<String, Integer> regionFilter) {
        return convertToBitmask(regionFilter, "region");
    }

    public static Integer convertType(Map<String, Integer> typeFilter) {
        return convertToBitmask(typeFilter, "type");
    }

    public static Integer convertPurpose(Map<String, Integer> purposeFilter) {
        return convertToBitmask(purposeFilter, "purpose");
    }

    public static Integer convertActivity(Map<String, Integer> activityFilter) {
        return convertToBitmask(activityFilter, "activity");
    }

    public static Integer convertEtc(Map<String, Integer> etcFilter) {
        return convertToBitmask(etcFilter, "etc");
    }

    public static String convertRegionToString(Integer bitmask) {
        return categoryBitmask.get("region").entrySet().stream()
                .filter(entry -> (bitmask & entry.getValue()) > 0)
                .map(Map.Entry::getKey)
                .findFirst()  // 첫 번째 매칭된 값만 반환
                .orElse("Unknown");
    }

    public static String convertTypeToString(Integer bitmask) {
        return categoryBitmask.get("type").entrySet().stream()
                .filter(entry -> (bitmask & entry.getValue()) > 0)
                .map(Map.Entry::getKey)
                .findFirst()  // 첫 번째 매칭된 값만 반환
                .orElse("Unknown");
    }

}

