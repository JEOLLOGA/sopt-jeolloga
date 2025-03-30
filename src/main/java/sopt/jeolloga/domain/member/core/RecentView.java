package sopt.jeolloga.domain.member.core;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sopt.jeolloga.domain.templestay.core.Templestay;

@Entity
@Table(name = "recent_view")
@Getter
@Setter
@NoArgsConstructor
public class RecentView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "templestay_id")
    private Templestay templestay;

    public RecentView(Member member, Templestay templestay) {
        this.member = member;
        this.templestay = templestay;
    }
}
