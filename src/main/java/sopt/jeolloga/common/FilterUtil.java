package sopt.jeolloga.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Component
public class FilterUtil {

    private static Map<String, Map<String, Integer>> categoryBitmask;

    // JSON 파일 로드
    static {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            categoryBitmask = objectMapper.readValue(Paths.get("src/main/java/sopt/jeolloga/common/Categories.json").toFile(), Map.class);
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

