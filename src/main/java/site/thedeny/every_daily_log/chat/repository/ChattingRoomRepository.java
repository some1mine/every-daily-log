package site.thedeny.every_daily_log.chat.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import site.thedeny.every_daily_log.chat.entity.ChattingRoomEntity;

public interface ChattingRoomRepository extends ReactiveCrudRepository<ChattingRoomEntity, String> {
}
