package sopt.jeolloga.common;

import java.util.HashMap;
import java.util.Map;

public class CategoryUtils {

    private static final Map<Integer, String> REGION_MAP = new HashMap<>();
    private static final Map<Integer, String> TYPE_MAP = new HashMap<>();

    static {
        REGION_MAP.put(1, "강원");
        REGION_MAP.put(2, "경기");
        REGION_MAP.put(4, "경남");
        REGION_MAP.put(8, "경북");
        REGION_MAP.put(16, "광주");
        REGION_MAP.put(32, "대구");
        REGION_MAP.put(64, "대전");
        REGION_MAP.put(128, "부산");
        REGION_MAP.put(256, "서울");
        REGION_MAP.put(512, "인천");
        REGION_MAP.put(1024, "전남");
        REGION_MAP.put(2048, "전북");
        REGION_MAP.put(4096, "제주");
        REGION_MAP.put(8192, "충남");
        REGION_MAP.put(16384, "충북");

        TYPE_MAP.put(1, "당일형");
        TYPE_MAP.put(2, "휴식형");
        TYPE_MAP.put(4, "체험형");
    }

    public static String getRegionName(int region) {
        return REGION_MAP.getOrDefault(region, "알 수 없음");
    }

    public static String getTypeName(int type) {
        return TYPE_MAP.getOrDefault(type, "알 수 없음");
    }
}
