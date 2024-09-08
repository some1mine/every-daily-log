package site.thedeny.every_daily_log.common.config.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import reactor.core.publisher.Mono;


public class LoginFailureHandler implements ServerAuthenticationFailureHandler {
    private final ServerRedirectStrategy redirectStrategy = new DefaultServerRedirectStrategy();
    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        if (exception instanceof BadCredentialsException) {
            return Mono.error(new RuntimeException("login failed"));
        }
        return webFilterExchange.getChain().filter(webFilterExchange.getExchange());
    }
}
