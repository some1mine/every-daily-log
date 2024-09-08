package site.thedeny.every_daily_log.chat.dto.request;

import site.thedeny.every_daily_log.chat.entity.ChattingRoomEntity;

public record ChattingRoomRequest(String memberKey, String roomName, String roomState) {
    public ChattingRoomEntity convertToRoomEntity() {
        return ChattingRoomEntity.builder().roomName(roomName).roomState(roomState).build();
    }
}
