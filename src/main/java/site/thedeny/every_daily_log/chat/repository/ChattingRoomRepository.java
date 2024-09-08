package site.thedeny.every_daily_log.chat.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import site.thedeny.every_daily_log.chat.entity.ChattingRoomEntity;

@Repository
public interface ChattingRoomRepository extends ReactiveCrudRepository<ChattingRoomEntity, String> {
}
