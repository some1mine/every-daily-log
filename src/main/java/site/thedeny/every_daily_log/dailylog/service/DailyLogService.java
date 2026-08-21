package site.thedeny.every_daily_log.dailylog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import site.thedeny.every_daily_log.common.member.repository.MemberRepository;
import site.thedeny.every_daily_log.dailylog.dto.request.DailyLogCreateRequest;
import site.thedeny.every_daily_log.dailylog.dto.request.DailyLogUpdateRequest;
import site.thedeny.every_daily_log.dailylog.dto.response.DailyLogResponse;
import site.thedeny.every_daily_log.dailylog.entity.DailyLogEntity;
import site.thedeny.every_daily_log.dailylog.entity.DailyLogVisibility;
import site.thedeny.every_daily_log.dailylog.repository.DailyLogRepository;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DailyLogService {

    private final DailyLogRepository dailyLogRepository;
    private final MemberRepository memberRepository;
    private final Clock clock = Clock.systemUTC();

    public Mono<DailyLogResponse> create(String memberKey, DailyLogCreateRequest request) {
        validate(request.logDate(), request.title(), request.content(), request.visibility());
        LocalDateTime now = LocalDateTime.now(clock);

        return memberRepository.findById(memberKey)
                .filter(member -> "Y".equalsIgnoreCase(member.getEnabled()))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "활성 회원만 기록할 수 있습니다.")))
                .then(dailyLogRepository.save(DailyLogEntity.builder()
                        .memberKey(memberKey)
                        .logDate(request.logDate())
                        .title(request.title().trim())
                        .content(request.content().trim())
                        .visibility(request.visibility())
                        .reactionCount(0)
                        .createdAt(now)
                        .updatedAt(now)
                        .build()))
                .map(DailyLogResponse::from);
    }

    public Flux<DailyLogResponse> getMine(String memberKey) {
        return dailyLogRepository.findAllByMemberKeyOrderByLogDateDesc(memberKey)
                .map(DailyLogResponse::from);
    }

    public Flux<DailyLogResponse> getPublicFeed() {
        return dailyLogRepository.findAllByVisibilityOrderByCreatedAtDesc(DailyLogVisibility.PUBLIC)
                .map(DailyLogResponse::from);
    }

    public Mono<DailyLogResponse> getDetail(String viewerKey, Long id) {
        return dailyLogRepository.findById(id)
                .switchIfEmpty(notFound())
                .filter(log -> log.getVisibility() == DailyLogVisibility.PUBLIC
                        || log.getMemberKey().equals(viewerKey))
                // 비공개 기록의 존재 여부도 타인에게 노출하지 않도록 404로 통일한다.
                .switchIfEmpty(notFound())
                .map(DailyLogResponse::from);
    }

    public Mono<DailyLogResponse> update(String memberKey, Long id, DailyLogUpdateRequest request) {
        validate(request.logDate(), request.title(), request.content(), request.visibility());
        return dailyLogRepository.findByIdAndMemberKey(id, memberKey)
                .switchIfEmpty(notFound())
                .flatMap(log -> {
                    log.setLogDate(request.logDate());
                    log.setTitle(request.title().trim());
                    log.setContent(request.content().trim());
                    log.setVisibility(request.visibility());
                    log.setUpdatedAt(LocalDateTime.now(clock));
                    return dailyLogRepository.save(log);
                })
                .map(DailyLogResponse::from);
    }

    public Mono<Void> delete(String memberKey, Long id) {
        return dailyLogRepository.findByIdAndMemberKey(id, memberKey)
                .switchIfEmpty(notFound())
                .flatMap(dailyLogRepository::delete);
    }

    private void validate(java.time.LocalDate logDate, String title, String content,
                          DailyLogVisibility visibility) {
        if (logDate == null || visibility == null || title == null || title.isBlank()
                || content == null || content.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "날짜, 제목, 내용, 공개 범위는 필수입니다.");
        }
        if (title.trim().length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "제목은 100자 이하여야 합니다.");
        }
        if (content.trim().length() > 5000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "내용은 5000자 이하여야 합니다.");
        }
    }

    private <T> Mono<T> notFound() {
        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "기록을 찾을 수 없습니다."));
    }
}
