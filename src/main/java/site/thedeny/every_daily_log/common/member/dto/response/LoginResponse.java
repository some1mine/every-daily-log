package site.thedeny.every_daily_log.common.member.dto.response;

/** 로그인 성공 시 클라이언트가 저장할 Bearer access token 정보다. */
public record LoginResponse(String tokenType, String accessToken, long expiresIn) {
}
