package site.thedeny.every_daily_log.dailylog.dto.response;

import site.thedeny.every_daily_log.dailylog.entity.DailyLogEntity;
import site.thedeny.every_daily_log.dailylog.entity.DailyLogVisibility;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DailyLogResponse(
        Long id,
        String memberKey,
        LocalDate logDate,
        String title,
        String content,
        DailyLogVisibility visibility,
        long reactionCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static DailyLogResponse from(DailyLogEntity entity) {
        return new DailyLogResponse(
                entity.getId(), entity.getMemberKey(), entity.getLogDate(), entity.getTitle(),
                entity.getContent(), entity.getVisibility(), entity.getReactionCount(),
                entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
