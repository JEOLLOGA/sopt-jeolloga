package sopt.jeolloga.domain.wishlist.core;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sopt.jeolloga.domain.member.Member;
import sopt.jeolloga.domain.templestay.core.Templestay;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "wishlist")
public class Wishlist {

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

    public Wishlist(Member member, Templestay templestay) {
        this.member = member;
        this.templestay = templestay;
    }
}
