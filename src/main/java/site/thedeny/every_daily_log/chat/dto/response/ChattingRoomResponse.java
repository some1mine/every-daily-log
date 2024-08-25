package site.thedeny.every_daily_log.chat.dto.response;

import site.thedeny.every_daily_log.chat.entity.ChattingRoomEntity;

import java.time.LocalDateTime;

public record ChattingRoomResponse(String id, String roomName, String roomState, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static ChattingRoomResponse fromEntity(ChattingRoomEntity entity) {
        return new ChattingRoomResponse(entity.getId(), entity.getRoomName(), entity.getRoomState(), entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
