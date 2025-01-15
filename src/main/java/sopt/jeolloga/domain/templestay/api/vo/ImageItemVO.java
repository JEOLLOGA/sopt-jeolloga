package sopt.jeolloga.domain.templestay.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // 알려지지 않은 필드를 무시
public class ImageItemVO {
    private String title;
    private String link;
    private String thumbnail;
}
