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
public class TemplestayImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "templestay_id")
    private Templestay templestay;

    @Column(name = "img_url")
    private String imgUrl;

    public TemplestayImage(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
