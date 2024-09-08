package site.thedeny.every_daily_log.common.member.entity;

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
    private String password;
    private String userName;
    private String nickname;
    private String introduce;
    private String reactionCnt;
    private String enabled;
    private LocalTime createdAt;
    private LocalTime updatedAt;
}
