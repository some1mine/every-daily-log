package site.thedeny.every_daily_log.chat.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import site.thedeny.every_daily_log.chat.entity.ChattingEntity;

@Repository
public interface ChattingRepository extends ReactiveMongoRepository<ChattingEntity, String> {
    @Tailable
    @Query("{ 'roomKey': ?0 }")
    Flux<ChattingEntity> findChatsInRoom(String roomKey);
    @Tailable
    @Query("{ $or: [ { 'senderKey': ?0 }, { 'receiverKey': ?0 } ] }")
    Flux<ChattingEntity> findMyChats(String memberKey);
    @Tailable
    @Query("{ 'roomKey': ?0, 'senderKey': ?1 }")
    Flux<ChattingEntity> findSentMessages(String roomKey, String senderKey);
    @Tailable
    @Query("{ 'roomKey': ?0, 'receiverKey': ?1 }")
    Flux<ChattingEntity> findReceivedMessages(String roomKey, String receiverKey);
}
