package site.thedeny.every_daily_log.chat.dto.request;

import site.thedeny.every_daily_log.chat.entity.ChattingEntity;

import java.time.LocalTime;

public record ChattingRequest(String roomKey, String senderKey, String receiverKey, String msg, String targetMemberKey) {
    public ChattingEntity convertToEntity() {
        return ChattingEntity.builder()
                .roomKey(roomKey).senderKey(senderKey).receiverKey(receiverKey).msg(msg).createdAt(LocalTime.now()).build();
    }
}
