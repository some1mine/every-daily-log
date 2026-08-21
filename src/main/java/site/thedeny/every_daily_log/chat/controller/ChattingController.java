package site.thedeny.every_daily_log.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import site.thedeny.every_daily_log.chat.dto.request.ChattingRequest;
import site.thedeny.every_daily_log.chat.dto.response.ChattingResponse;
import site.thedeny.every_daily_log.chat.dto.response.ChattingRoomResponse;
import site.thedeny.every_daily_log.chat.entity.ChattingEntity;
import site.thedeny.every_daily_log.chat.service.ChattingService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/chatting")
public class ChattingController {
    private final ChattingService chattingService;

    @CrossOrigin
    @GetMapping(value = "/my/{targetMemberKey}/rooms", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<ChattingRoomResponse>> getMyRooms(@PathVariable String targetMemberKey) {
        System.out.println("targetMemberKey = " + targetMemberKey);
        return ResponseEntity.ok(chattingService.getMyRooms(targetMemberKey));
    }

    @CrossOrigin
    @GetMapping(value = "/chats/{roomKey}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<ChattingResponse>> getChatsInRoom(@PathVariable String roomKey) {
        System.out.println("roomKey = " + roomKey);
        return ResponseEntity.ok(chattingService.getChatsInRoom(roomKey));
    }

    @CrossOrigin
    @GetMapping(value = "/my/{targetMemberKey}/rooms/{roomKey}/sent")
    public ResponseEntity<Flux<ChattingResponse>> getMySentMessages(@PathVariable String targetMemberKey,
                                                                    @PathVariable String roomKey) {
        System.out.println("targetMemberKey = " + targetMemberKey);
        System.out.println("roomKey = " + roomKey);
        return ResponseEntity.ok(chattingService.getSentMessages(roomKey, targetMemberKey));
    }

    @CrossOrigin
    @GetMapping(value = "/my/{targetMemberKey}/rooms/{roomKey}/received")
    public ResponseEntity<Flux<ChattingResponse>> getMyReceivedMessages(@PathVariable String targetMemberKey,
                                                                        @PathVariable String roomKey) {
        System.out.println("targetMemberKey = " + targetMemberKey);
        System.out.println("roomKey = " + roomKey);
        return ResponseEntity.ok(chattingService.getReceivedMessages(roomKey, targetMemberKey));
    }

    @PostMapping("/send")
    public ResponseEntity<Mono<ChattingEntity>> sendMessage(@AuthenticationPrincipal Jwt jwt,
                                                             @RequestBody ChattingRequest request) {
        // TODO [CHAT-05] 현재는 201만 반환하고 실제 저장을 하지 않는다.
        // body(chattingService.sendChat(request))를 연결한다.
        // 그 다음 request.senderKey를 신뢰하지 말고 로그인 principal의 회원 key를 사용한다.
        // 완료 테스트: POST 후 반환된 id로 MongoDB 문서를 조회할 수 있어야 한다.
        ChattingRequest authenticatedRequest = new ChattingRequest(
                request.roomKey(), jwt.getClaimAsString("memberKey"), request.receiverKey(),
                request.msg(), request.targetMemberKey());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chattingService.sendChat(authenticatedRequest));
    }

}
