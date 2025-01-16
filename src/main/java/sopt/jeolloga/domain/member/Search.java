package sopt.jeolloga.domain.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "search")
@Getter
@NoArgsConstructor
public class Search {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "content", length = 45)
    private String content;

    public Search(Member member, String content) {
        this.member = member;
        this.content = content;
    }
}

