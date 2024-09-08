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
@Document(collection = "chatting")
public class ChattingEntity {
    @Id private String id;
    private String roomKey;
    private String senderKey;
    private String receiverKey;
    private String msg;
    private LocalTime createdAt;
}
