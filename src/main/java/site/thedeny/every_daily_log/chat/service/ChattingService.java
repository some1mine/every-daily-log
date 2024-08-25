package site.thedeny.every_daily_log.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import site.thedeny.every_daily_log.chat.dto.request.ChattingRequest;
import site.thedeny.every_daily_log.chat.dto.response.ChattingResponse;
import site.thedeny.every_daily_log.chat.entity.ChattingEntity;
import site.thedeny.every_daily_log.chat.repository.ChattingRepository;

@Service
@RequiredArgsConstructor
public class ChattingService {
    private final ChattingRepository chattingRepository;

    public Flux<String> getMyRooms(String targetMemberKey) {
        return chattingRepository.findMyRooms(targetMemberKey)
                .map(ChattingEntity::getRoomKey).distinct()
                .doOnNext(System.out::println)
                .defaultIfEmpty("");
    }

    public Flux<ChattingResponse> getChatsInRoom(String roomKey) {
        return chattingRepository
                .findChatsInRoom(roomKey)
                .map(ChattingResponse::fromEntity)
                .defaultIfEmpty(ChattingResponse.fromEntity(new ChattingEntity()))
                .subscribeOn(Schedulers.boundedElastic());
    }
    public Flux<ChattingResponse> getSentMessages(String roomKey, String senderKey) {
        return chattingRepository.findSentMessages(roomKey, senderKey)
                .map(ChattingResponse::fromEntity)
                .defaultIfEmpty(ChattingResponse.fromEntity(new ChattingEntity()));
    }

    public Flux<ChattingResponse> getReceivedMessages(String roomKey, String receiverKey) {
        return chattingRepository.findReceivedMessages(roomKey, receiverKey)
                .map(ChattingResponse::fromEntity)
                .defaultIfEmpty(ChattingResponse.fromEntity(new ChattingEntity()));
    }

    public Mono<ChattingResponse> sendChat(ChattingRequest request) {
        ChattingEntity receivedEntity = request.convertToEntity();
        Mono<ChattingEntity> savedEntity = chattingRepository.save(receivedEntity);
        return savedEntity.map(ChattingResponse::fromEntity);
    }

}
