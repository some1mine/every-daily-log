package site.thedeny.every_daily_log.common.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import site.thedeny.every_daily_log.common.member.dto.request.MemberRequest;
import site.thedeny.every_daily_log.common.member.entity.MemberEntity;
import site.thedeny.every_daily_log.common.member.service.MemberService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final ReactiveAuthenticationManager authenticationManager;
    private final ServerSecurityContextRepository securityContextRepository;

    /* 회원가입 */
    @PostMapping("/join")
    public ResponseEntity<Mono<MemberEntity>> join(@RequestBody MemberRequest request) {
        System.out.println("request = " + request);
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.join(request));
    }

    @PostMapping("/login")
    public Mono<Void> login(ServerWebExchange exchange) {
        return exchange.getPrincipal()
                .cast(Authentication.class)
                .flatMap(authentication -> {
                    SecurityContext context = new SecurityContextImpl(authentication);
                    return securityContextRepository
                            .save(exchange, context)
                            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
                })
                .onErrorResume(e -> {
                    // Log error and return meaningful response
                    return Mono.error(new RuntimeException("Login failed", e));
                });
    }
}
