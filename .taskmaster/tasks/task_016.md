# Task ID: 16

**Title:** 코드 리팩토링

**Status:** in-progress

**Dependencies:** None

**Priority:** high

**Description:** 기존 리팩토링 작업 전체를 하나의 에픽으로 통합 관리

**Details:**

기존 Task 1~15를 모두 SubTask로 이동해 단일 상위 작업으로 관리합니다.

**Test Strategy:**

각 서브태스크 완료 시 회귀 테스트를 수행하고 전체 검증을 실행합니다.

## Subtasks

### 16.1. 공통 경로 파싱 유틸로 중복 제거

**Status:** done  
**Dependencies:** None  

Repository 경로/namespace 파싱 로직을 단일 유틸 또는 도메인 서비스로 통합한다.

**Details:**

ExploreController, RepositoryDetailService, NamespaceDetailService, DashboardService에 흩어진 parse/trim/lastSegment 로직을 공통화하고 단위 테스트를 추가한다.

### 16.2. PAT 만료일 필드 전달 누락 수정

**Status:** done  
**Dependencies:** None  

SettingsController의 expiration 검증 결과가 실제 요청 DTO로 전달되도록 수정한다.

**Details:**

PersonalAccessTokenForm.expiration을 UserCredentialIssueRequest 및 서버 API 계약에 맞게 반영하고, 폼-DTO-클라이언트 경로를 일치시킨다.

### 16.3. 로그 설정 정상화

**Status:** done  
**Dependencies:** None  

logback 루트 레벨 OFF를 환경별 합리적 레벨로 조정하고 debug=true를 제거한다.

**Details:**

local은 INFO/DEBUG, 비로컬은 INFO/WARN 기반으로 조정하고 불필요한 과다 로그를 줄인다.

### 16.4. SecurityConfig 정규식 매처 단순화

**Status:** done  
**Dependencies:** None  

복잡한 RegexRequestMatcher 기반 접근 제어를 명시적인 패턴/구성으로 단순화한다.

**Details:**

공개 라우트/인증 라우트를 분리 정의하고 신규 라우트 추가 시 회귀를 줄이는 구조로 바꾼다.

### 16.5. HTTP 로그 민감정보 보호

**Status:** done  
**Dependencies:** 16.3  

HttpLogFilter의 응답 바디 로깅 정책을 안전하게 개선한다.

**Details:**

마스킹 규칙, 최대 길이, 환경별 on/off, 정적 리소스 제외 정책을 재정의한다.

### 16.6. SOPS 환경 로더 안정성 강화

**Status:** done  
**Dependencies:** None  

SopsEnvironmentPostProcessor의 프로세스 실행/예외 처리를 견고하게 만든다.

**Details:**

System.out 제거, 타임아웃/에러스트림 처리, 실패 메시지 표준화, 프로파일별 동작 정리

### 16.7. 주석 처리된 레거시 코드 정리

**Status:** done  
**Dependencies:** None  

사용하지 않는 주석 파일/코드 블록을 삭제하거나 문서화된 대체 경로로 이동한다.

**Details:**

HttpLogFilterOld, JgitkinsAdapter, RepositoryController 내 대형 주석 블록 정리

### 16.8. RepositoryController 비대화 해소

**Status:** done  
**Dependencies:** 16.1  

RepositoryController 책임을 분리해 가독성과 유지보수성을 개선한다.

**Details:**

검증, 사용자 프로필 해석, view model 구성, request 매핑 로직을 support/service로 분리

### 16.9. JGitkinsServerClient 에러 처리 일관화

**Status:** done  
**Dependencies:** None  

예외 처리/응답 매핑 스타일을 일관화하고 catch(Exception) 범위를 축소한다.

**Details:**

공통 에러 매핑 메서드 도입, API 오류 메시지 표준화, null 반환 축소

### 16.10. Bean Validation 도입

**Status:** pending  
**Dependencies:** 16.8  

폼 검증 로직을 @Valid + Constraint 어노테이션 기반으로 전환한다.

**Details:**

RepositoryCreateForm, OrganizeCreateForm, PersonalAccessTokenForm에 제약조건 추가 및 BindingResult 처리

### 16.11. 오류 메시지 i18n 통합

**Status:** pending  
**Dependencies:** 16.10  

영/한 혼재 문자열을 메시지 소스로 통합한다.

**Details:**

messages.properties, messages_ko.properties를 도입하고 컨트롤러 하드코딩 문자열 제거

### 16.12. Dashboard 커밋 조회 N+1 완화

**Status:** pending  
**Dependencies:** 16.1  

리포지토리별 커밋 조회 반복 호출을 줄여 응답 성능을 개선한다.

**Details:**

배치 API 도입 또는 조회 제한/비동기 전략으로 latency를 줄인다.

### 16.13. 세션 접근 코드 정리

**Status:** pending  
**Dependencies:** None  

중복 getSession(true/false) 호출과 세션 키 접근 패턴을 정리한다.

**Details:**

UsernameSetupController, OAuth2LoginSuccessHandler, SessionSupport 주변 중복 제거

### 16.14. 테스트 베이스라인 구축

**Status:** pending  
**Dependencies:** 16.1, 16.2, 16.8, 16.9, 16.10  

서비스/컨트롤러 핵심 경로에 최소 회귀 테스트 세트를 구축한다.

**Details:**

Repository/Settings/Organize 흐름 중심으로 단위+MVC 테스트 추가

### 16.15. Gradle 의존성 스코프 정리

**Status:** pending  
**Dependencies:** None  

devtools 등 런타임 영향 의존성의 스코프를 재정의한다.

**Details:**

spring-boot-devtools를 developmentOnly로 이동하고 빌드 산출물 영향도를 점검
