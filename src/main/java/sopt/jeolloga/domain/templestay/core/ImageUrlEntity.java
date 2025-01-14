package sopt.jeolloga.domain.templestay.core;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "templestay_image")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ImageUrlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "templestay_id")
    private Long templestayId;

    @Column(name = "img_url")
    private String imgUrl;

    public ImageUrlEntity(Long id, Long templestayId, String imgUrl) {
        this.id = id;
        this.templestayId = templestayId;
        this.imgUrl = imgUrl;
    }
}
