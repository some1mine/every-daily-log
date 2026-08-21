package site.thedeny.every_daily_log.common.member.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import site.thedeny.every_daily_log.common.config.security.JwtTokenService;
import site.thedeny.every_daily_log.common.member.dto.request.LoginForm;
import site.thedeny.every_daily_log.common.member.dto.request.MemberRequest;
import site.thedeny.every_daily_log.common.member.dto.response.LoginResponse;
import site.thedeny.every_daily_log.common.member.entity.MemberEntity;
import site.thedeny.every_daily_log.common.member.repository.MemberRepository;
import site.thedeny.every_daily_log.common.member.service.MemberService;

@RestController
@RequestMapping("/auth")
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final JwtTokenService jwtTokenService;
    private final ReactiveAuthenticationManager loginAuthenticationManager;

    public MemberController(
            MemberService memberService,
            MemberRepository memberRepository,
            JwtTokenService jwtTokenService,
            @Qualifier("loginAuthenticationManager") ReactiveAuthenticationManager loginAuthenticationManager
    ) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.jwtTokenService = jwtTokenService;
        this.loginAuthenticationManager = loginAuthenticationManager;
    }

    @PostMapping("/join")
    public ResponseEntity<Mono<MemberEntity>> join(@RequestBody MemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.join(request));
    }

    /** 아이디/비밀번호를 검증하고 JWT access token을 발급한다. */
    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResponse>> login(@RequestBody LoginForm form) {
        UsernamePasswordAuthenticationToken credentials =
                new UsernamePasswordAuthenticationToken(form.getUserId(), form.getPassword());

        return loginAuthenticationManager.authenticate(credentials)
                // memberKey claim을 넣기 위해 인증된 userId로 회원을 다시 조회한다.
                .flatMap(authentication -> memberRepository.findByUserId(authentication.getName()))
                .map(jwtTokenService::createAccessToken)
                .map(ResponseEntity::ok);
    }

    /**
     * JWT는 서버에 로그인 상태를 저장하지 않는다. 클라이언트가 토큰을 폐기하면 된다.
     * 즉시 강제 로그아웃이 필요하면 refresh token 저장소나 deny-list를 추가한다.
     */
    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout() {
        return Mono.just(ResponseEntity.noContent().build());
    }
}
