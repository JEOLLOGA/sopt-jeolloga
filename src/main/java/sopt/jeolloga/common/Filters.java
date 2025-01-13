package sopt.jeolloga.common;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class Filters {

    private List<String> region;
    private List<String> type;
    private List<String> purpose;
    private List<String> activity;
    private List<String> etc;

    public Filters() {
        this.region = Arrays.asList("강원", "경기", "경남", "경북", "광주", "대구", "대전", "부산", "서울", "인천", "전남", "전북", "제주", "충남", "충북");
        this.type = Arrays.asList("당일형", "휴식형", "체험형");
        this.purpose = Arrays.asList("힐링", "전통문화 체험", "심신치유", "자기계발", "여행 일정", "사찰순례", "휴식", "호기심", "기타");
        this.activity = Arrays.asList("발우공양", "108배", "스님과의 차담", "등산", "새벽 예불", "사찰 탐방", "염주 만들기", "연등 만들기", "다도", "명상", "산책", "요가", "기타");
        this.etc = Arrays.asList("절밥이 맛있는", "TV에 나온", "연예인이 다녀간", "근처 관광지가 많은", "속세와 멀어지고 싶은", "동물 친구들과 함께", "유튜브 운영 중인", "단체 가능");
    }

    public List<String> getRegion() {
        return region;
    }

    public List<String> getType() {
        return type;
    }

    public List<String> getPurpose() {
        return purpose;
    }

    public List<String> getActivity() {
        return activity;
    }

    public List<String> getEtc() {
        return etc;
    }
}
