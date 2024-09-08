package site.thedeny.every_daily_log.common.member.controller;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import site.thedeny.every_daily_log.common.config.security.SecurityConfig;
import site.thedeny.every_daily_log.common.member.dto.request.LoginForm;
import site.thedeny.every_daily_log.common.member.dto.request.MemberRequest;
import site.thedeny.every_daily_log.common.member.repository.MemberRepository;
import site.thedeny.every_daily_log.common.member.service.MemberService;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(value = {SpringExtension.class, MockitoExtension.class})
@WebFluxTest(controllers = MemberController.class)
@ContextConfiguration(classes = SecurityConfig.class)
class MemberControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @InjectMocks
    private MemberService memberService;

    @MockBean
    private MemberRepository memberRepository;

    @Test
    @Order(1)
    void join() {
        MemberRequest request = new MemberRequest("testId", "testPassword", "testName", "testNickname", "null");

        Mockito.when(memberRepository.save(request.convertToEntity())).thenReturn(Mono.just(request.convertToEntity()));

        webTestClient.post()
                .uri("/auth/join")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(request))
                .exchange()
                .expectStatus().isCreated();

        Mockito.verify(memberRepository, Mockito.times(1)).save(request.convertToEntity());
    }

    @Test
    @Order(2)
    void login() {
        LoginForm loginForm = new LoginForm("testId", "testPassword");
        webTestClient.post()
                .uri("/auth/login")
                .body(Mono.just(loginForm), LoginForm.class)
                .exchange()
                .expectStatus().isOk();
    }
}