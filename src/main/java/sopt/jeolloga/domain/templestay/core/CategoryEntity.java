package sopt.jeolloga.domain.templestay.core;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @OneToOne(fetch = FetchType.LAZY)
//    @ToString.Exclude
//    @JoinColumn(name = "templestay_id", nullable = false)
//    private TemplestayEntity templestay;

    @Column(name = "templestay_id")
    private Long templestayId;

    @Column(name = "region")
    private Integer region; // 지역

    @Column(name = "type")
    private Integer type; // 유형

    @Column(name = "purpose")
    private Integer purpose; // 목적

    @Column(name = "activity")
    private Integer activity; // 체험

    @Column(name = "price")
    private Integer price; // 가격

    @Column(name = "etc")
    private Integer etc; // 기타

    public CategoryEntity(Long id, Long templestayId, int region, int type, int purpose, int activity, int price, int etc) {
        this.id = id;
        this.templestayId = templestayId;
        this.region = region;
        this.type = type;
        this.purpose = purpose;
        this.activity = activity;
        this.price = price;
        this.etc = etc;
    }
}
