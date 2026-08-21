package site.thedeny.every_daily_log.dailylog.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import site.thedeny.every_daily_log.common.member.entity.MemberEntity;
import site.thedeny.every_daily_log.common.member.repository.MemberRepository;
import site.thedeny.every_daily_log.dailylog.dto.request.DailyLogCreateRequest;
import site.thedeny.every_daily_log.dailylog.dto.request.DailyLogUpdateRequest;
import site.thedeny.every_daily_log.dailylog.entity.DailyLogEntity;
import site.thedeny.every_daily_log.dailylog.entity.DailyLogVisibility;
import site.thedeny.every_daily_log.dailylog.repository.DailyLogRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyLogServiceTest {

    @Mock DailyLogRepository dailyLogRepository;
    @Mock MemberRepository memberRepository;
    private DailyLogService service;

    @BeforeEach
    void setUp() {
        service = new DailyLogService(dailyLogRepository, memberRepository);
    }

    @Test
    void createUsesAuthenticatedMemberKey() {
        var request = new DailyLogCreateRequest(LocalDate.of(2026, 8, 21), "오늘", "기록", DailyLogVisibility.PUBLIC);
        when(memberRepository.findById("member-1")).thenReturn(Mono.just(activeMember("member-1")));
        when(dailyLogRepository.save(any())).thenAnswer(invocation -> {
            DailyLogEntity saved = invocation.getArgument(0);
            saved.setId(1L);
            return Mono.just(saved);
        });

        StepVerifier.create(service.create("member-1", request))
                .assertNext(result -> {
                    assertThat(result.id()).isEqualTo(1L);
                    assertThat(result.memberKey()).isEqualTo("member-1");
                    assertThat(result.reactionCount()).isZero();
                })
                .verifyComplete();
    }

    @Test
    void privateLogIsHiddenFromAnotherMember() {
        when(dailyLogRepository.findById(1L)).thenReturn(Mono.just(log("owner", DailyLogVisibility.PRIVATE)));

        StepVerifier.create(service.getDetail("other", 1L))
                .expectErrorMatches(error -> error instanceof ResponseStatusException exception
                        && exception.getStatusCode().value() == 404)
                .verify();
    }

    @Test
    void ownerCanUpdateWithoutChangingOwnershipOrReactionCount() {
        DailyLogEntity entity = log("owner", DailyLogVisibility.PUBLIC);
        entity.setReactionCount(7);
        when(dailyLogRepository.findByIdAndMemberKey(1L, "owner")).thenReturn(Mono.just(entity));
        when(dailyLogRepository.save(entity)).thenReturn(Mono.just(entity));
        var request = new DailyLogUpdateRequest(LocalDate.of(2026, 8, 22), "수정", "수정 내용", DailyLogVisibility.PRIVATE);

        StepVerifier.create(service.update("owner", 1L, request))
                .assertNext(result -> {
                    assertThat(result.memberKey()).isEqualTo("owner");
                    assertThat(result.reactionCount()).isEqualTo(7);
                    assertThat(result.title()).isEqualTo("수정");
                })
                .verifyComplete();
    }

    @Test
    void nonOwnerCannotDelete() {
        when(dailyLogRepository.findByIdAndMemberKey(1L, "other")).thenReturn(Mono.empty());

        StepVerifier.create(service.delete("other", 1L))
                .expectError(ResponseStatusException.class)
                .verify();
        verify(dailyLogRepository, never()).delete(any());
    }

    @Test
    void publicFeedPreservesRepositoryOrder() {
        DailyLogEntity first = log("a", DailyLogVisibility.PUBLIC);
        first.setId(2L);
        DailyLogEntity second = log("b", DailyLogVisibility.PUBLIC);
        second.setId(1L);
        when(dailyLogRepository.findAllByVisibilityOrderByCreatedAtDesc(DailyLogVisibility.PUBLIC))
                .thenReturn(Flux.just(first, second));

        StepVerifier.create(service.getPublicFeed())
                .assertNext(result -> assertThat(result.id()).isEqualTo(2L))
                .assertNext(result -> assertThat(result.id()).isEqualTo(1L))
                .verifyComplete();
    }

    private MemberEntity activeMember(String id) {
        return MemberEntity.builder().id(id).userId("user").enabled("Y").build();
    }

    private DailyLogEntity log(String owner, DailyLogVisibility visibility) {
        LocalDateTime now = LocalDateTime.now();
        return DailyLogEntity.builder()
                .id(1L).memberKey(owner).logDate(LocalDate.now()).title("제목").content("내용")
                .visibility(visibility).reactionCount(0).createdAt(now).updatedAt(now).build();
    }
}
