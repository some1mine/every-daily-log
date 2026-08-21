package site.thedeny.every_daily_log.common.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import site.thedeny.every_daily_log.common.member.dto.response.LoginResponse;
import site.thedeny.every_daily_log.common.member.entity.MemberEntity;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final long accessTokenSeconds;
    private final Clock clock;

    @Autowired
    public JwtTokenService(
            JwtEncoder jwtEncoder,
            @Value("${security.jwt.access-token-seconds}") long accessTokenSeconds
    ) {
        this(jwtEncoder, accessTokenSeconds, Clock.systemUTC());
    }

    JwtTokenService(JwtEncoder jwtEncoder, long accessTokenSeconds, Clock clock) {
        this.jwtEncoder = jwtEncoder;
        this.accessTokenSeconds = accessTokenSeconds;
        this.clock = clock;
    }

    public LoginResponse createAccessToken(MemberEntity member) {
        Instant issuedAt = clock.instant();
        Instant expiresAt = issuedAt.plusSeconds(accessTokenSeconds);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("every-daily-log")
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(member.getUserId())
                // 소유권 검사는 요청 body가 아니라 이 서명된 memberKey claim을 사용한다.
                .claim("memberKey", member.getId())
                .claim("roles", List.of(member.getEnabled()))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new LoginResponse("Bearer", token, accessTokenSeconds);
    }
}
