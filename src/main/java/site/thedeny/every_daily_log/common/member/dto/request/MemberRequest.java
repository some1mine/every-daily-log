package site.thedeny.every_daily_log.common.member.dto.request;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import site.thedeny.every_daily_log.common.member.entity.MemberEntity;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequest {

    private String userId;
    private String password;
    private String userName;
    private String nickname;
    private String introduce;
    public MemberEntity convertToEntity() {
        return MemberEntity.builder().userId(userId).password(password).userName(userName).nickname(nickname).introduce(introduce).build();
    }
}
