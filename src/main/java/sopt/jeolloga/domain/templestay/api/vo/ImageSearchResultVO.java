package sopt.jeolloga.domain.templestay.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import sopt.jeolloga.domain.templestay.api.vo.ImageItemVO;

import java.util.List;

@ToString
@NoArgsConstructor
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true) // 알 수 없는 JSON 필드를 무시
public class ImageSearchResultVO {
    private String lastBuildDate; // 추가
    private List<ImageItemVO> items;
}
