package sopt.jeolloga.domain.member.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByKakaoUserId(Long KakaoUserId);
//    Optional<Member> findById(Long id);
}
