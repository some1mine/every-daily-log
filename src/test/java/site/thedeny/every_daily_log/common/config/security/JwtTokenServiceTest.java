package site.thedeny.every_daily_log.common.config.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import reactor.test.StepVerifier;
import site.thedeny.every_daily_log.common.member.entity.MemberEntity;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenServiceTest {

    private static final String SECRET = "test-secret-key-for-hs256-must-be-at-least-32-bytes";

    @Test
    void issuedTokenCanBeVerifiedAndContainsMemberClaims() {
        SecretKey key = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        // Decoder는 현재 시각 기준으로 exp를 검사하므로 테스트 실행 시각을 사용한다.
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        JwtTokenService service = new JwtTokenService(
                new NimbusJwtEncoder(new ImmutableSecret<>(key)),
                3600,
                Clock.fixed(now, java.time.ZoneOffset.UTC)
        );

        MemberEntity member = MemberEntity.builder()
                .id("member-1")
                .userId("user1")
                .enabled("Y")
                .build();

        var response = service.createAccessToken(member);
        var decoder = NimbusReactiveJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(3600);

        StepVerifier.create(decoder.decode(response.accessToken()))
                .assertNext(jwt -> {
                    assertThat(jwt.getSubject()).isEqualTo("user1");
                    assertThat(jwt.getClaimAsString("memberKey")).isEqualTo("member-1");
                    assertThat(jwt.getClaimAsStringList("roles")).containsExactly("Y");
                    assertThat(jwt.getIssuedAt()).isEqualTo(now);
                    assertThat(jwt.getExpiresAt()).isEqualTo(now.plusSeconds(3600));
                })
                .verifyComplete();
    }
}
