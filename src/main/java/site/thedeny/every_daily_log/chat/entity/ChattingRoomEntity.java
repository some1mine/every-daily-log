package site.thedeny.every_daily_log.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("CHAT_ROOM")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChattingRoomEntity {
    @Id private String id;
    private String roomName;
    private String roomState;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
