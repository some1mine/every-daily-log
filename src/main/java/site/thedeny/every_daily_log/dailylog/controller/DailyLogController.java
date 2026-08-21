package site.thedeny.every_daily_log.dailylog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import site.thedeny.every_daily_log.dailylog.dto.request.DailyLogCreateRequest;
import site.thedeny.every_daily_log.dailylog.dto.request.DailyLogUpdateRequest;
import site.thedeny.every_daily_log.dailylog.dto.response.DailyLogResponse;
import site.thedeny.every_daily_log.dailylog.service.DailyLogService;

@RestController
@RequestMapping("/daily-logs")
@RequiredArgsConstructor
public class DailyLogController {

    private final DailyLogService dailyLogService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<DailyLogResponse> create(@AuthenticationPrincipal Jwt jwt,
                                         @RequestBody DailyLogCreateRequest request) {
        return dailyLogService.create(memberKey(jwt), request);
    }

    @GetMapping("/me")
    public Flux<DailyLogResponse> getMine(@AuthenticationPrincipal Jwt jwt) {
        return dailyLogService.getMine(memberKey(jwt));
    }

    @GetMapping
    public Flux<DailyLogResponse> getPublicFeed() {
        return dailyLogService.getPublicFeed();
    }

    @GetMapping("/{id}")
    public Mono<DailyLogResponse> getDetail(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return dailyLogService.getDetail(memberKey(jwt), id);
    }

    @PutMapping("/{id}")
    public Mono<DailyLogResponse> update(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                                         @RequestBody DailyLogUpdateRequest request) {
        return dailyLogService.update(memberKey(jwt), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return dailyLogService.delete(memberKey(jwt), id);
    }

    private String memberKey(Jwt jwt) {
        return jwt.getClaimAsString("memberKey");
    }
}
