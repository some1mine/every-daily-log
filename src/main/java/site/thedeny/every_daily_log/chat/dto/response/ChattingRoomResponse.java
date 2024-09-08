package site.thedeny.every_daily_log.chat.dto.response;

import site.thedeny.every_daily_log.chat.entity.ChattingRoomEntity;

import java.time.LocalTime;

public record ChattingRoomResponse(String id, String roomName, String roomState, LocalTime createdAt, LocalTime updatedAt) {
    public static ChattingRoomResponse fromEntity(ChattingRoomEntity entity) {
        return new ChattingRoomResponse(entity.getId(), entity.getRoomName(), entity.getRoomState(), entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
