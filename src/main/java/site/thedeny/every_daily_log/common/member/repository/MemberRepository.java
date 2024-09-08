package site.thedeny.every_daily_log.common.member.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import site.thedeny.every_daily_log.common.member.entity.MemberEntity;

import java.lang.reflect.Member;

@Repository
public interface MemberRepository extends ReactiveCrudRepository<MemberEntity, String> {
    Mono<MemberEntity> findByUserId(String userId);
    Mono<Boolean> existsByUserId(String userId);
}
