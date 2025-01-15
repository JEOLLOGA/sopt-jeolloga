package sopt.jeolloga.domain.member.api.repository;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    private Long id;

    @Column(name = "kakao_user_id", nullable = false)
    private Long kakaoUserId;

    @Column(nullable = false, length = 45)
    private String nickname;

    @Column(nullable = false, length = 45, unique = true)
    private String email;

    @Column(name = "age_range", length = 45)
    private String ageRange;

    @Column(length = 45)
    private String gender;

    @Column(length = 45)
    private String religion;

    @Column(name = "has_experience")
    private Boolean hasExperience;

    public Member(Long kakaoUserId, String email, String nickname){
        this.kakaoUserId = kakaoUserId;
        this.email = email;
        this.nickname = nickname;
    }

}
