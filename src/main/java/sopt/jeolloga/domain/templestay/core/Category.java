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
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "templestay_id")
    private Long templestayId;

    @Column(name = "region")
    private Integer region;

    @Column(name = "type")
    private Integer type;

    @Column(name = "purpose")
    private Integer purpose;

    @Column(name = "activity")
    private Integer activity;

    @Column(name = "price")
    private Integer price;

    @Column(name = "etc")
    private Integer etc;

    public Category(int region, int type, int purpose, int activity, int price, int etc) {
        this.region = region;
        this.type = type;
        this.purpose = purpose;
        this.activity = activity;
        this.price = price;
        this.etc = etc;
    }
}