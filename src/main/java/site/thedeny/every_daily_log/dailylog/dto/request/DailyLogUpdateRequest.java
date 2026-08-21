package site.thedeny.every_daily_log.dailylog.dto.request;

import site.thedeny.every_daily_log.dailylog.entity.DailyLogVisibility;

import java.time.LocalDate;

public record DailyLogUpdateRequest(
        LocalDate logDate,
        String title,
        String content,
        DailyLogVisibility visibility
) {
}
