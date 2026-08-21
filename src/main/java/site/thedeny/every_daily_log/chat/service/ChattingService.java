package site.thedeny.every_daily_log.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import site.thedeny.every_daily_log.chat.dto.request.ChattingRequest;
import site.thedeny.every_daily_log.chat.dto.response.ChattingResponse;
import site.thedeny.every_daily_log.chat.dto.response.ChattingRoomResponse;
import site.thedeny.every_daily_log.chat.entity.ChattingEntity;
import site.thedeny.every_daily_log.chat.entity.ChattingRoomEntity;
import site.thedeny.every_daily_log.chat.repository.ChattingRepository;
import site.thedeny.every_daily_log.chat.repository.ChattingRelationRepository;
import site.thedeny.every_daily_log.chat.repository.ChattingRoomRepository;

import java.util.Comparator;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChattingService {
    private final ChattingRepository chattingRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    private final ChattingRelationRepository chattingRelationRepository;

    public Flux<ChattingRoomResponse> getMyRooms(String memberKey) {
        /*
         * TODO [CHAT-02] 이 메서드를 완전히 non-blocking으로 다시 작성한다.
         * 권장 흐름:
         * findMyChats(memberKey) -> map(roomKey) -> distinct()
         * -> chattingRoomRepository.findById(roomKey) (flatMap 또는 concatMap)
         * -> map(ChattingRoomResponse::fromEntity)
         *
         * collectSortedList().block()은 WebFlux 이벤트 루프를 막으므로 제거한다.
         * 빈 결과일 때 가짜 빈 엔티티를 반환하지 말고 Flux.empty()를 그대로 반환한다.
         */
        return chattingRepository.findMyChats(memberKey)
                .map(ChattingEntity::getRoomKey)
                .filter(Objects::nonNull)
                .distinct()
                .sort(Comparator.reverseOrder())
                .concatMap(chattingRoomRepository::findById)
                .map(ChattingRoomResponse::fromEntity);
    }

    public Flux<ChattingResponse> getChatsInRoom(String roomKey) {
        // TODO [CHAT-03] createdAt 기준 정렬을 repository query에 추가한다.
        // 빈 방은 null 필드 DTO 한 건이 아니라 Flux.empty()여야 한다.
        // 완료 테스트: 메시지 0건/1건/여러 건과 시간순 정렬을 StepVerifier로 확인한다.
        return chattingRepository.findChatsInRoom(roomKey)
                .map(ChattingResponse::fromEntity);
    }
    public Flux<ChattingResponse> getSentMessages(String roomKey, String senderKey) {
        return chattingRepository.findSentMessages(roomKey, senderKey)
                .map(ChattingResponse::fromEntity);
    }

    public Flux<ChattingResponse> getReceivedMessages(String roomKey, String receiverKey) {
        return chattingRepository.findReceivedMessages(roomKey, receiverKey)
                .map(ChattingResponse::fromEntity);
    }

    public Mono<ChattingEntity> sendChat(ChattingRequest request) {
        // TODO [CHAT-04] 저장 전에 방 존재 여부와 sender/receiver의 방 참여 여부를 확인한다.
        // 검증 실패는 400/403/404로 구분 가능한 도메인 예외로 만든다.
        // 저장 성공 시 생성된 message id를 반환한다.


        return requireTrue(chattingRoomRepository.existsById(request.roomKey()), "Not Existing Chatting Room")
                .then(requireTrue(chattingRelationRepository.existsByRoomKeyAndMemberKey(
                        request.roomKey(), request.senderKey()), "Sender Is Not In Chatting Room"))
                .then(requireTrue(chattingRelationRepository.existsByRoomKeyAndMemberKey(
                        request.roomKey(), request.receiverKey()), "Receiver Is Not In Chatting Room"))
                .then(chattingRepository.save(request.convertToEntity()));
    }

    private Mono<Void> requireTrue(Mono<Boolean> result, String message) {
        return result.filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(message)))
                .then();
    }

}
