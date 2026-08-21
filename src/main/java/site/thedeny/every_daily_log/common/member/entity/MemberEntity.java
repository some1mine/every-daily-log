package site.thedeny.every_daily_log.common.member.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MEMBER")
public class MemberEntity {
    @Id private String id;
    private String userId;
    // 회원가입 응답 등에 엔티티가 직렬화되더라도 password hash를 노출하지 않는다.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String userName;
    private String nickname;
    private String introduce;
    private String reactionCnt;
    private String enabled;
    private LocalTime createdAt;
    private LocalTime updatedAt;
}
