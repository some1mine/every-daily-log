package site.thedeny.every_daily_log.dailylog.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("DAILY_LOG")
public class DailyLogEntity {
    @Id private Long id;
    private String memberKey;
    private LocalDate logDate;
    private String title;
    private String content;
    private DailyLogVisibility visibility;
    private long reactionCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
