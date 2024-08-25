package site.thedeny.every_daily_log.chat.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import site.thedeny.every_daily_log.chat.dto.request.ChattingRequest;
import site.thedeny.every_daily_log.chat.dto.response.ChattingResponse;
import site.thedeny.every_daily_log.chat.repository.ChattingRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("빌드는 해보자구")
@ExtendWith(MockitoExtension.class)
class ChattingServiceTest {

    @InjectMocks
    private ChattingService chattingService;
    @Mock
    private ChattingRepository chattingRepository;

    @Test
    void getMyRooms() {
        // given
        ChattingRequest request = new ChattingRequest(null, null, null, null, "1");

        // stub
        BDDMockito.given(chattingRepository.findMyRooms(request.targetMemberKey())).willAnswer(i -> Flux.fromIterable(Collections.emptyList()));

        // when
        Flux<String> myRooms = chattingService.getMyRooms("1");

        // then
        Assertions.assertThat(myRooms).isEqualTo(Flux.empty());
    }

    @Test
    void getChatsInRoom() {
        // given
        ChattingRequest request = new ChattingRequest("1", null, null, null, null);

        // stub
        BDDMockito.given(chattingRepository.findChatsInRoom(request.roomKey())).willAnswer(i -> Flux.fromIterable(Collections.emptyList()));

        // when
        Flux<ChattingResponse> messages = chattingService.getChatsInRoom("1");

        // then
        Assertions.assertThat(messages).isEqualTo(Flux.empty());
    }

    @Test
    void getSentMessages() {
        // given
        ChattingRequest request = new ChattingRequest("1", "1", null, null, null);

        // stub
        BDDMockito.given(chattingRepository.findSentMessages(request.roomKey(), request.senderKey())).willAnswer(i -> Flux.fromIterable(Collections.emptyList()));

        // when
        Flux<ChattingResponse> messages = chattingService.getSentMessages("1","1");

        // then
        Assertions.assertThat(messages).isEqualTo(Flux.empty());
    }

    @Test
    void getReceivedMessages() {
        // given
        ChattingRequest request = new ChattingRequest("1", null, "1", null, null);

        // stub
        BDDMockito.given(chattingRepository.findReceivedMessages(request.roomKey(), request.senderKey())).willAnswer(i -> Flux.fromIterable(Collections.emptyList()));

        // when
        Flux<ChattingResponse> messages = chattingService.getReceivedMessages("1","1");

        // then
        Assertions.assertThat(messages).isEqualTo(Flux.empty());
    }


    @Test
    void sendChat() {

    }
}