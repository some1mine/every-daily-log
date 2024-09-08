package site.thedeny.every_daily_log.common.member.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginForm {
    private String userId;
    private String password;
}
