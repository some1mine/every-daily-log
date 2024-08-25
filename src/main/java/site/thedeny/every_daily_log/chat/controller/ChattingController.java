package site.thedeny.every_daily_log.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import site.thedeny.every_daily_log.chat.dto.request.ChattingRequest;
import site.thedeny.every_daily_log.chat.dto.response.ChattingResponse;
import site.thedeny.every_daily_log.chat.service.ChattingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatting")
public class ChattingController {
    private final ChattingService chattingService;

    @CrossOrigin
    @GetMapping(value = "/my/{targetMemberKey}/rooms", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<String>> getMyRooms(@PathVariable String targetMemberKey) {
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
    public ResponseEntity<Mono<ChattingResponse>> sendMessage(@RequestBody ChattingRequest request) {
        System.out.println("request = " + request);
        return ResponseEntity.ok(chattingService.sendChat(request));
    }

}
