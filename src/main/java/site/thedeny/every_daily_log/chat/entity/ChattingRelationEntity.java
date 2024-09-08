package site.thedeny.every_daily_log.chat.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chatting_relation")
public class ChattingRelationEntity {
    @Id private String id;
    private String roomKey;
    private String memberKey;
    private LocalTime enteredAt;
    private LocalTime exitedAt;
}
