package site.thedeny.every_daily_log.common.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import site.thedeny.every_daily_log.common.member.dto.request.LoginForm;
import site.thedeny.every_daily_log.common.member.repository.MemberRepository;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@EnableReactiveMethodSecurity
public class SecurityConfig {
    //    private final SecurityContextRepository securityContextRepository;
    private final MemberRepository memberRepository;
    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(a -> {
                    a.pathMatchers("/auth/**", "/logout").permitAll();
                    a.anyExchange().authenticated();
                })
                .exceptionHandling(e -> e.
                        authenticationEntryPoint(authenticationEntryPoint()))
                .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .httpBasic(h -> h.authenticationEntryPoint(authenticationEntryPoint()))
                .securityContextRepository(serverSecurityContextRepository())
//                .formLogin(l -> l.loginPage("/login"))
                .logout(l -> l.logoutHandler((exchange, authentication) -> {
                    exchange.getExchange().getAttributes().remove("bodyString");
                    authentication.setAuthenticated(false);
                    return null;
                }))
                .build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(MemberRepository repository) {
        return username -> repository.findByUserId(username)
                .map(m -> User.builder().username(m.getUserId())
                        .password(m.getPassword())
                        .authorities(List.of(new SimpleGrantedAuthority(m.getEnabled())))
                        .build());
    }

    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    @Bean
    public AuthenticationWebFilter authenticationWebFilter() {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(authenticationManager());

        filter.setSecurityContextRepository(serverSecurityContextRepository());
        filter.setServerAuthenticationConverter(authenticationConverter());
        filter.setRequiresAuthenticationMatcher(
                ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/login")
        );

        return filter;
    }

    @Bean
    public ServerAccessDeniedHandler accessDeniedHandler() {
        return new ServerAccessDeniedHandler() {
            @Override
            public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.FORBIDDEN);
                return new AuthFailureHandler().formatResponse(response, denied);
            }
        };
    }

    @Bean
    public ServerAuthenticationEntryPoint authenticationEntryPoint() {
        return new ServerAuthenticationEntryPoint() {
            @Override
            public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return new AuthFailureHandler().formatResponse(response, ex);
            }
        };
    }

    @Bean
    public ServerSecurityContextRepository serverSecurityContextRepository() {
        return NoOpServerSecurityContextRepository.getInstance();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager authManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService(memberRepository));
        authManager.setPasswordEncoder(passwordEncoder());
        return authManager;
    }

    @Bean
    public ServerAuthenticationConverter authenticationConverter(){
        return exchange -> exchange.getRequest().getBody()
                .distinct()
                .reduce(new StringBuilder(), (builder, buffer) -> {
                    byte[] bytes = new byte[buffer.readableByteCount()];
                    buffer.read(bytes);
                    DataBufferUtils.release(buffer);
                    return builder.append(new String(bytes, StandardCharsets.UTF_8));
                })
                .flatMap(bodyString -> {
                    try {
                        System.out.println("bodyString = " + bodyString);
                        ObjectMapper objectMapper = new ObjectMapper();
                        LoginForm member = objectMapper.readValue(bodyString.toString(), LoginForm.class);
                        Authentication data = new UsernamePasswordAuthenticationToken(member.getUserId(), member.getPassword());
                        exchange.getAttributes().put("bodyString", bodyString);
                        return Mono.just(data);
                    } catch (Exception e) {
                        exchange.getAttributes().remove("bodyString");
                        return Mono.error(new RuntimeException("Login parse error", e));
                    }
                });
    }
}
