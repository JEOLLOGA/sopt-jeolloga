package sopt.jeolloga.domain.templestay.core;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "templestay")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class TemplestayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "templestay_name", length = 255, nullable = false)
    private String templestayName;

    @Column(name = "organized_name", length = 255)
    private String organizedName;

    @Column(name = "phone_number", length = 45)
    private String phoneNumber;

    @Column(columnDefinition = "JSON")
    private String introduction;

    @Column(length = 45)
    private String address;

    @Column(length = 255)
    private String youtube;

    @Column(name = "temple_name", length = 45)
    private String templeName;

    @Column(columnDefinition = "JSON")
    private String schedule;

    @Column(precision = 10, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 6)
    private BigDecimal longitude;

    @Column(name = "tag", length = 255)
    private String tag;

    public TemplestayEntity(Long id, String templestayName, String organizedName, String phoneNumber, String introduction, String address, String youtube, String templeName, String schedule, BigDecimal latitude, BigDecimal longitude, String tag) {
        this.id = id;
        this.templestayName = templestayName;
        this.organizedName = organizedName;
        this.phoneNumber = phoneNumber;
        this.introduction = introduction;
        this.address = address;
        this.youtube = youtube;
        this.templeName = templeName;
        this.schedule = schedule;
        this.latitude = latitude;
        this.longitude = longitude;
        this.tag = tag;
    }
}

