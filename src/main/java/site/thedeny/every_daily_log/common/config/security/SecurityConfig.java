package site.thedeny.every_daily_log.common.config.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import site.thedeny.every_daily_log.common.member.repository.MemberRepository;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberRepository memberRepository;

    /**
     * JWT 방식의 실제 보안 설정이다.
     *
     * 1. /auth/join과 /auth/login만 익명 접근을 허용한다.
     * 2. 로그인 성공 시 MemberController가 access token을 발급한다.
     * 3. 클라이언트는 이후 요청에 "Authorization: Bearer {token}"을 보낸다.
     * 4. Resource Server 필터가 서명과 만료 시간을 검사하고 Authentication을 만든다.
     * 5. 서버는 로그인 세션을 저장하지 않는다.
     */
    @Bean
    public SecurityWebFilterChain securityFilterChain(
            ServerHttpSecurity http,
            ReactiveAuthenticationManager jwtAuthenticationManager
    ) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/auth/join", "/auth/login").permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .authenticationManagerResolver(exchange ->
                                reactor.core.publisher.Mono.just(jwtAuthenticationManager)))
                .build();
    }

    /** 아이디/비밀번호 로그인에서 사용하는 인증 관리자다. */
    @Bean(name = "loginAuthenticationManager")
    public ReactiveAuthenticationManager loginAuthenticationManager(
            ReactiveUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        UserDetailsRepositoryReactiveAuthenticationManager manager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        manager.setPasswordEncoder(passwordEncoder);
        return manager;
    }

    /** Bearer JWT를 검증한 뒤 SecurityContext에 넣을 Authentication을 만든다. */
    @Bean
    @Primary
    public ReactiveAuthenticationManager jwtAuthenticationManager(
            NimbusReactiveJwtDecoder jwtDecoder
    ) {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix("");

        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt ->
                reactor.core.publisher.Flux.fromIterable(authoritiesConverter.convert(jwt)));

        JwtReactiveAuthenticationManager manager = new JwtReactiveAuthenticationManager(jwtDecoder);
        manager.setJwtAuthenticationConverter(converter);
        return manager;
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return userId -> memberRepository.findByUserId(userId)
                .map(member -> User.builder()
                        .username(member.getUserId())
                        .password(member.getPassword())
                        .authorities(List.of(new SimpleGrantedAuthority(member.getEnabled())))
                        .disabled(!"Y".equalsIgnoreCase(member.getEnabled()))
                        .build());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecretKey jwtSecretKey(@Value("${security.jwt.secret}") String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("security.jwt.secret must be at least 32 bytes for HS256");
        }
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey));
    }

    @Bean
    public NimbusReactiveJwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
        return NimbusReactiveJwtDecoder.withSecretKey(jwtSecretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    /*
     * ----------------------------------------------------------------------
     * 세션 방식 예시 — JWT 대신 세션을 선택할 때 아래 형태로 교체한다.
     * ----------------------------------------------------------------------
     *
     * @Bean
     * public ServerSecurityContextRepository sessionSecurityContextRepository() {
     *     return new WebSessionServerSecurityContextRepository();
     * }
     *
     * @Bean
     * public SecurityWebFilterChain sessionSecurityFilterChain(
     *         ServerHttpSecurity http,
     *         ServerSecurityContextRepository sessionRepository
     * ) {
     *     return http
     *             .csrf(ServerHttpSecurity.CsrfSpec::disable)
     *             .securityContextRepository(sessionRepository)
     *             .authorizeExchange(exchange -> exchange
     *                     .pathMatchers("/auth/join", "/auth/login").permitAll()
     *                     .anyExchange().authenticated())
     *             .build();
     * }
     *
     * 세션 로그인 Controller의 핵심 흐름:
     *
     * return loginAuthenticationManager.authenticate(authenticationToken)
     *     .flatMap(authentication -> {
     *         SecurityContext context = new SecurityContextImpl(authentication);
     *         return sessionRepository.save(exchange, context);
     *     });
     *
     * 세션 로그아웃의 핵심 흐름:
     *
     * return exchange.getExchange().getSession().flatMap(WebSession::invalidate);
     *
     * 세션 방식은 브라우저가 SESSION 쿠키를 보관한다. JWT 방식의 JwtEncoder,
     * JwtDecoder, oauth2ResourceServer 설정은 제거해야 한다. 두 방식을 섞지 않는다.
     */
}
