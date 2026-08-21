package site.thedeny.every_daily_log.dailylog;

/**
 * 그 날의 기록 기능 구현 가이드.
 *
 * 이 클래스는 실행 기능을 가지지 않는다. 아래 TODO를 번호순으로 구현하고,
 * 각 단계의 완료 조건을 테스트로 확인하기 위한 체크리스트다.
 * 기능 구현이 끝나면 이 파일을 삭제해도 된다.
 */
final class DailyLogImplementationGuide {

    private DailyLogImplementationGuide() {
    }

    /*
     * TODO [LOG-01] 먼저 저장 모델을 정한다.
     *
     * 새 dailylog/entity/DailyLogEntity.java를 만들고 R2DBC 테이블에 대응시킨다.
     * 최소 필드:
     *   id, memberKey, logDate, title, content, visibility,
     *   reactionCount, createdAt, updatedAt
     *
     * 날짜가 핵심이므로 logDate는 LocalDate, 생성/수정 시각은 LocalDateTime을 사용한다.
     * reactionCount는 문자열이 아닌 long/int를 사용한다.
     * 한 회원이 같은 날짜에 여러 기록을 쓸 수 있는지도 여기서 결정한다.
     * 한 개만 허용한다면 DB에 UNIQUE(member_key, log_date)를 추가한다.
     *
     * 완료 조건: 빈 H2에서 애플리케이션을 시작했을 때 DAILY_LOG 테이블이 생성된다.
     */

    /*
     * TODO [LOG-02] API용 DTO를 엔티티와 분리한다.
     *
     * dailylog/dto/request/DailyLogCreateRequest:
     *   logDate, title, content, visibility만 받는다.
     * memberKey와 reactionCount를 요청에서 받으면 다른 사용자를 사칭하거나 공감 수를 조작할 수 있다.
     *
     * dailylog/dto/response/DailyLogResponse:
     *   id, author 정보, 날짜, 제목, 내용, 공개 범위, 공감 수, 생성/수정 시각을 반환한다.
     *
     * 완료 조건: 필수 값 누락과 제목/내용 길이 초과가 400 응답이 되는 테스트를 작성한다.
     */

    /*
     * TODO [LOG-03] Repository를 만든다.
     *
     * ReactiveCrudRepository<DailyLogEntity, Long>를 상속하고 다음 조회를 추가한다.
     *   findByIdAndMemberKey(...)                         // 수정/삭제 소유권 검사
     *   findAllByMemberKeyOrderByLogDateDesc(...)        // 내 기록
     *   findAllByVisibilityOrderByCreatedAtDesc(...)     // 공개 피드
     *
     * 페이지가 커질 수 있으므로 최종적으로 Pageable 또는 id 기반 cursor pagination을 적용한다.
     * 완료 조건: repository slice test에서 저장, 단건 조회, 정렬, 비공개 제외를 검증한다.
     */

    /*
     * TODO [LOG-04] Service의 create부터 구현한다.
     *
     * create(authenticatedMemberKey, request)의 순서:
     *   1. 로그인 사용자 존재/활성 여부 확인
     *   2. 요청 값 검증
     *   3. authenticatedMemberKey로 엔티티 생성
     *   4. repository.save(entity)
     *   5. DailyLogResponse로 변환
     *
     * Controller에서 받은 memberKey나 nickname을 작성자 식별에 사용하지 않는다.
     * 완료 조건: 저장된 memberKey가 항상 principal의 회원 key와 같아야 한다.
     */

    /*
     * TODO [LOG-05] 조회를 구현한다.
     *
     * getMine(memberKey): 공개 여부와 관계없이 자신의 기록을 날짜 역순으로 반환한다.
     * getPublicFeed(): PUBLIC 기록만 최신순으로 반환한다.
     * getDetail(viewerKey, logId): 작성자 본인이거나 PUBLIC인 경우에만 본문을 반환한다.
     *
     * 존재하지 않는 기록은 404, 권한 없는 비공개 기록은 정책에 따라 403 또는 404로 통일한다.
     * 완료 조건: 본인/타인 × 공개/비공개 조합 네 가지를 모두 테스트한다.
     */

    /*
     * TODO [LOG-06] 수정과 삭제를 구현한다.
     *
     * 먼저 findByIdAndMemberKey(logId, authenticatedMemberKey)로 소유권을 확인한다.
     * 조회 후 요청의 변경 허용 필드만 적용하고 저장한다. memberKey/reactionCount는 변경하지 않는다.
     * 삭제도 같은 소유권 검사를 거친다.
     * 완료 조건: 다른 회원의 id로 수정/삭제할 때 데이터가 바뀌지 않고 403/404가 반환된다.
     */

    /*
     * TODO [LOG-07] WebFlux Controller를 만든다.
     *
     * 권장 API:
     *   POST   /daily-logs           기록 생성
     *   GET    /daily-logs/me        내 기록 목록
     *   GET    /daily-logs           공개 피드
     *   GET    /daily-logs/{id}      상세 조회
     *   PUT    /daily-logs/{id}      내 기록 수정
     *   DELETE /daily-logs/{id}      내 기록 삭제
     *
     * principal -> memberKey 변환은 공통 메서드/컴포넌트로 만들고 모든 쓰기 API에서 재사용한다.
     * Controller는 Mono/Flux를 block하지 않고 Service 결과를 그대로 반환한다.
     */

    /*
     * TODO [LOG-08] 통합 테스트로 첫 번째 완성 범위를 고정한다.
     *
     * 최소 시나리오:
     *   회원가입 -> 로그인 -> 기록 생성 -> 내 목록 조회 -> 상세 조회 -> 수정 -> 삭제
     * 추가 시나리오:
     *   비로그인 생성 401, 타인 비공개 조회 차단, 타인 수정/삭제 차단, 공개 피드 정렬
     *
     * 이 테스트들이 통과한 뒤에만 공감, 댓글, 이미지 업로드를 별도 기능으로 추가한다.
     */
}
