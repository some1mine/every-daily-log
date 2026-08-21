package site.thedeny.every_daily_log.dailylog.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import site.thedeny.every_daily_log.dailylog.entity.DailyLogEntity;
import site.thedeny.every_daily_log.dailylog.entity.DailyLogVisibility;

public interface DailyLogRepository extends ReactiveCrudRepository<DailyLogEntity, Long> {
    Mono<DailyLogEntity> findByIdAndMemberKey(Long id, String memberKey);
    Flux<DailyLogEntity> findAllByMemberKeyOrderByLogDateDesc(String memberKey);
    Flux<DailyLogEntity> findAllByVisibilityOrderByCreatedAtDesc(DailyLogVisibility visibility);
}
