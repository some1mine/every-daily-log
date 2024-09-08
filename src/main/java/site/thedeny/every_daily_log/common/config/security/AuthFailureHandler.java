package site.thedeny.every_daily_log.common.config.security;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

public class AuthFailureHandler {
    public Mono<Void> formatResponse(ServerHttpResponse response, Exception e) {
        try {
            response.getHeaders()
                    .add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
//            Map<String, Integer> responseData = Map.of("statusCode", response.getStatusCode().value(), "Access Denied", null);
            return response.writeWith(Mono.error(new RuntimeException(e.getMessage())));
        } catch (RuntimeException ex) {
            return Mono.error(new RuntimeException("Auth Failed"));
        }
    }
}
