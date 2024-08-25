package site.thedeny.every_daily_log.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import site.thedeny.every_daily_log.chat.dto.request.ChattingRequest;
import site.thedeny.every_daily_log.chat.dto.response.ChattingResponse;
import site.thedeny.every_daily_log.chat.dto.response.ChattingRoomResponse;
import site.thedeny.every_daily_log.chat.entity.ChattingEntity;
import site.thedeny.every_daily_log.chat.entity.ChattingRoomEntity;
import site.thedeny.every_daily_log.chat.repository.ChattingRepository;
import site.thedeny.every_daily_log.chat.repository.ChattingRoomRepository;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChattingService {
    private final ChattingRepository chattingRepository;
    private final ChattingRoomRepository chattingRoomRepository;

    public Flux<ChattingRoomResponse> getMyRooms(String memberKey) {
        List<String> rooms = chattingRepository.findMyChats(memberKey)
                .map(ChattingEntity::getRoomKey)
                .defaultIfEmpty("")
                .distinct()
                .collectSortedList(Comparator.reverseOrder())
                .block();

        if (rooms == null || rooms.isEmpty()) return Flux.empty();

        return chattingRoomRepository
                .findAllById(rooms)
                .defaultIfEmpty(new ChattingRoomEntity())
                .map(ChattingRoomResponse::fromEntity)
                .subscribeOn(Schedulers.boundedElastic());
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

    public void sendChat(ChattingRequest request) {
        chattingRepository.insert(request.convertToEntity());
    }

}
