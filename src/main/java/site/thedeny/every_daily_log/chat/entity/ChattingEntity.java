package site.thedeny.every_daily_log.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
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
    private LocalDateTime createdAt;
}
