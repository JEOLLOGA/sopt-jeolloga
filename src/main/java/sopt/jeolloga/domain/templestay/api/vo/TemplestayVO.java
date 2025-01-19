package sopt.jeolloga.domain.templestay.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TemplestayVO {
    private String title;
    private String description;
    private String bloggername;
    private String postdate;
    private String link;
    private String thumbnail;

    public TemplestayVO(String title, String description, String bloggername, String postdate, String link, String thumbnail) {
        this.title = title;
        this.description = description;
        this.bloggername = bloggername;
        this.postdate = postdate;
        this.link = link;
        this.thumbnail = thumbnail;
    }
}
