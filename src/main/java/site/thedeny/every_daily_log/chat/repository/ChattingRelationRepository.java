package site.thedeny.every_daily_log.chat.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import site.thedeny.every_daily_log.chat.entity.ChattingRelationEntity;

public interface ChattingRelationRepository extends ReactiveMongoRepository<ChattingRelationEntity, String> {
    Mono<Boolean> existsByRoomKeyAndMemberKey(String roomKey, String memberKey);
}
