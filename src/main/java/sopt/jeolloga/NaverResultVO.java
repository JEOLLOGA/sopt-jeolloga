package sopt.jeolloga;

import lombok.*;

import java.util.List;

@ToString
@NoArgsConstructor
@Setter
@Getter
public class NaverResultVO {
    private String lastBuildDate;
    private int total;
    private int start;
    private int display;
    private List<TemplestayVO> items;

    public NaverResultVO(String lastBuildDate, int total, int start, int display, List<TemplestayVO> items) {
        this.lastBuildDate = lastBuildDate;
        this.total = total;
        this.start = start;
        this.display = display;
        this.items = items;
    }
}
