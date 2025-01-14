package sopt.jeolloga.domain.member.login.repository;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member") // 연결될 테이블 이름
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    private Long id; // 사용자 ID

    @Column(name = "kakao_user_id", nullable = false)
    private Long kakaoUserId; // 카카오 사용자 ID

    @Column(nullable = false, length = 45)
    private String nickname; // 사용자 닉네임

    @Column(nullable = false, length = 45, unique = true)
    private String email; // 이메일

    @Column(name = "age_range", length = 45)
    private String ageRange; // 연령대

    @Column(length = 45)
    private String gender; // 성별

    @Column(length = 45)
    private String religion; // 종교

    @Column(name = "has_experience")
    private Boolean hasExperience; // 템플스테이 경험 여부

    public Member(Long kakaoUserId, String email, String nickname){
        this.kakaoUserId = kakaoUserId;
        this.email = email;
        this.nickname = nickname;
    }

}
