package site.thedeny.every_daily_log.chat.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import site.thedeny.every_daily_log.chat.entity.ChattingEntity;

@Repository
public interface ChattingRepository extends ReactiveMongoRepository<ChattingEntity, String> {
    /*
     * TODO [CHAT-01] 조회 API를 먼저 일반 Flux 조회로 완성한다.
     * @Tailable은 MongoDB capped collection에서만 동작하고 스트림이 끝나지 않는다.
     * 최초 구현에서는 네 메서드의 @Tailable을 제거하고 정렬된 일반 조회를 사용한다.
     * 실시간 채팅이 필요해진 뒤 capped collection 생성과 재연결 정책을 추가하고 @Tailable을 복원한다.
     */
    @Query("{ 'roomKey': ?0 }")
    Flux<ChattingEntity> findChatsInRoom(String roomKey);
    @Query("{ $or: [ { 'senderKey': ?0 }, { 'receiverKey': ?0 } ] }")
    Flux<ChattingEntity> findMyChats(String memberKey);
    @Query("{ 'roomKey': ?0, 'senderKey': ?1 }")
    Flux<ChattingEntity> findSentMessages(String roomKey, String senderKey);
    @Query("{ 'roomKey': ?0, 'receiverKey': ?1 }")
    Flux<ChattingEntity> findReceivedMessages(String roomKey, String receiverKey);
}
