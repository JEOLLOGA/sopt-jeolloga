package sopt.jeolloga.domain.templestay.core;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "temple_name", length = 45)
    private String templeName;

    @Column(name = "review_title", length = 255)
    private String reviewTitle;

    @Column(name = "review_description", length = 255)
    private String reviewDescription;

    @Column(name = "review_name", length = 45)
    private String reviewName;

    @Column(name = "review_date", length = 45)
    private String reviewDate;

    @Column(name = "review_rink", length = 500)
    private String reviewRink;

    @Column(name = "review_img_url", length = 500)
    private String reviewImgUrl;

    public Review(String templeName, String reviewTitle, String reviewDescription, String reviewName, String reviewDate, String reviewRink, String reviewImgUrl) {
        this.templeName = templeName;
        this.reviewTitle = reviewTitle;
        this.reviewDescription = reviewDescription;
        this.reviewName = reviewName;
        this.reviewDate = reviewDate;
        this.reviewRink = reviewRink;
        this.reviewImgUrl = reviewImgUrl;
    }
}
