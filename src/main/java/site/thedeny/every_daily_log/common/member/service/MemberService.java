package site.thedeny.every_daily_log.common.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import site.thedeny.every_daily_log.common.member.dto.request.MemberRequest;
import site.thedeny.every_daily_log.common.member.entity.MemberEntity;
import site.thedeny.every_daily_log.common.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /* 회원 가입 */
    public Mono<MemberEntity> join(MemberRequest request) {
        System.out.println("request.getPassword() = " + request.getPassword());
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        MemberEntity entity = request.convertToEntity();
        System.out.println("entity = " + entity);
        return memberRepository.existsByUserId(entity.getUserId())
                .flatMap(exists -> {
                    if (exists) return Mono.error(new RuntimeException("이미 존재하는 아이디입니다."));
                    return memberRepository.save(entity);
                });
    }

    /* 내 프로필 조회 */
}
