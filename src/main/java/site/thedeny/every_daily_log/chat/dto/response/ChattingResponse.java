package site.thedeny.every_daily_log.chat.dto.response;

import site.thedeny.every_daily_log.chat.entity.ChattingEntity;

import java.time.LocalDateTime;

public record ChattingResponse(String id, String roomKey, String senderKey, String receiverKey, String msg, LocalDateTime createdAt) {
    public static ChattingResponse fromEntity(ChattingEntity entity){
        return new ChattingResponse(entity.getId(), entity.getRoomKey(), entity.getSenderKey(), entity.getReceiverKey(), entity.getMsg(), entity.getCreatedAt());
    }
}
