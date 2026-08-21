package site.thedeny.every_daily_log.common.member.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import site.thedeny.every_daily_log.common.config.security.JwtTokenService;
import site.thedeny.every_daily_log.common.member.dto.request.LoginForm;
import site.thedeny.every_daily_log.common.member.dto.request.MemberRequest;
import site.thedeny.every_daily_log.common.member.dto.response.LoginResponse;
import site.thedeny.every_daily_log.common.member.entity.MemberEntity;
import site.thedeny.every_daily_log.common.member.repository.MemberRepository;
import site.thedeny.every_daily_log.common.member.service.MemberService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @Mock MemberService memberService;
    @Mock MemberRepository memberRepository;
    @Mock JwtTokenService jwtTokenService;
    @Mock ReactiveAuthenticationManager loginAuthenticationManager;

    private MemberController controller;

    @BeforeEach
    void setUp() {
        controller = new MemberController(
                memberService,
                memberRepository,
                jwtTokenService,
                loginAuthenticationManager
        );
    }

    @Test
    void joinReturnsCreatedMember() {
        MemberRequest request = new MemberRequest("testId", "password", "name", "nickname", null);
        MemberEntity member = request.convertToEntity();
        when(memberService.join(request)).thenReturn(Mono.just(member));

        var response = controller.join(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        StepVerifier.create(response.getBody()).expectNext(member).verifyComplete();
    }

    @Test
    void loginAuthenticatesPasswordAndReturnsJwt() {
        LoginForm form = new LoginForm("testId", "password");
        var authentication = UsernamePasswordAuthenticationToken.authenticated(
                "testId", null, java.util.List.of());
        MemberEntity member = MemberEntity.builder()
                .id("member-1")
                .userId("testId")
                .enabled("Y")
                .build();
        LoginResponse token = new LoginResponse("Bearer", "signed.jwt.token", 3600);

        when(loginAuthenticationManager.authenticate(any())).thenReturn(Mono.just(authentication));
        when(memberRepository.findByUserId("testId")).thenReturn(Mono.just(member));
        when(jwtTokenService.createAccessToken(member)).thenReturn(token);

        StepVerifier.create(controller.login(form))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(response.getBody()).isEqualTo(token);
                })
                .verifyComplete();
    }
}
