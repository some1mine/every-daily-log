package site.thedeny.every_daily_log.chat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import site.thedeny.every_daily_log.chat.dto.request.ChattingRequest;
import site.thedeny.every_daily_log.chat.dto.response.ChattingResponse;
import site.thedeny.every_daily_log.chat.dto.response.ChattingRoomResponse;
import site.thedeny.every_daily_log.chat.entity.ChattingRoomEntity;
import site.thedeny.every_daily_log.chat.repository.ChattingRepository;
import site.thedeny.every_daily_log.chat.repository.ChattingRoomRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ChattingServiceTest {

    @InjectMocks
    private ChattingService chattingService;
    @Mock
    private ChattingRepository chattingRepository;
    @Mock
    private ChattingRoomRepository chattingRoomRepository;

    ChattingRequest request1 = new ChattingRequest("1", "1", "5", "tmp1", null);
    ChattingRequest request2 = new ChattingRequest("1", "2", "4", "tmp2", null);
    ChattingRequest request3 = new ChattingRequest("1", "3", "3", "tmp3", null);
    ChattingRequest request4 = new ChattingRequest("1", "4", "2", "tmp4", null);
    ChattingRequest request5 = new ChattingRequest("1", "5", "1", "tmp5", null);

    ChattingRoomEntity chattingRoom = ChattingRoomEntity.builder()
            .id("1")
            .roomName("tmp1")
            .roomState("1")
            .build();

    @BeforeEach
    void setUp() {
        chattingRoomRepository.save(chattingRoom);

        chattingRepository.saveAll(
                List.of(request1.convertToEntity(), request2.convertToEntity(), request3.convertToEntity(), request4.convertToEntity(), request5.convertToEntity()));
    }


    @Test
    void getMyRooms() {
        // given
        ChattingRequest request = new ChattingRequest("1", "1", "1", "tmp", "1");

        // stub
        Mockito.when(chattingRepository.findMyChats(request.targetMemberKey()))
                .thenReturn(Flux.just(request1.convertToEntity(), request5.convertToEntity()));

        Mockito.when(chattingRoomRepository.findAllById(List.of("1")))
                .thenReturn(Flux.just(chattingRoom));

        // when
        Flux<ChattingRoomResponse> result = chattingService.getMyRooms(request.targetMemberKey());

        // then
        StepVerifier.create(result)
                .expectNext(ChattingRoomResponse.fromEntity(ChattingRoomEntity.builder()
                        .id("1")
                        .roomName("tmp1")
                        .roomState("1")
                        .build()))
                .verifyComplete();
    }

    @Test
    void getChatsInRoom() {
        // given
        ChattingRequest request = new ChattingRequest("1", null, null, "tmp", null);

        // stub
        Mockito.when(chattingRepository.findChatsInRoom(request.roomKey()))
                .thenReturn(Flux.just(request1.convertToEntity(), request5.convertToEntity()));
        // when
        Flux<ChattingResponse> result = chattingService.getChatsInRoom(request.roomKey());

        // then
        StepVerifier.create(result)
                .expectNext(ChattingResponse.fromEntity(request1.convertToEntity()))
                .expectNext(ChattingResponse.fromEntity(request5.convertToEntity()))
                .verifyComplete();
    }

    @Test
    void getSentMessages() {
        // given
        ChattingRequest request = new ChattingRequest("1", "1", null, null, null);

        // stub
        Mockito.when(chattingRepository.findSentMessages(request.roomKey(), request.senderKey()))
                .thenReturn(Flux.just(request1.convertToEntity()));

        // when
        Flux<ChattingResponse> result = chattingService.getSentMessages(request.roomKey(), request.senderKey());

        // then
        StepVerifier.create(result)
                .expectNext(ChattingResponse.fromEntity(request1.convertToEntity()))
                .verifyComplete();
    }

    @Test
    void getReceivedMessages() {
        // given
        ChattingRequest request = new ChattingRequest("1", null, "1", null, null);

        // stub
        Mockito.when(chattingRepository.findReceivedMessages(request.roomKey(), request.receiverKey()))
                .thenReturn(Flux.just(request1.convertToEntity()));

        // when
        Flux<ChattingResponse> result = chattingService.getReceivedMessages(request.roomKey(), request.receiverKey());

        // then

        StepVerifier.create(result)
                .expectNext(ChattingResponse.fromEntity(request1.convertToEntity()))
                .verifyComplete();
    }

    @Test
    void sendChat() {
        // given
        ChattingRequest request = new ChattingRequest("1", "1", "5", "tmp1", null);

        // stub
        Mockito.when(chattingRepository.save(request.convertToEntity()))
                .thenReturn(Mono.just(request1.convertToEntity()));

        // when
        chattingService.sendChat(request);

        // then
        StepVerifier.create(chattingRepository.findById("6"))
                .expectNext(request1.convertToEntity())
                .verifyComplete();
    }
}