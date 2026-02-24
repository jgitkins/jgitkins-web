# Task ID: 2

**Title:** 리팩토링

**Status:** in-progress

**Dependencies:** None

**Priority:** high

**Description:** 구조 개선, 품질 개선, 테스트 체계 강화 작업

**Details:**


카테고리 기반 상위 Task

**Test Strategy:**


하위 작업별 회귀 테스트

## Subtasks

### 2.1. [1] 코드 리팩토링

**Status:** done  

**Dependencies:** None  


기존 리팩토링 작업 전체를 하나의 에픽으로 통합 관리

**Details:**


기존 Task 1~15를 모두 SubTask로 이동해 단일 상위 작업으로 관리합니다.


(legacy task: 1)

### 2.2. 애플리케이션 계층 인프라 의존성 제거 (캐시 로직 분리)

**Status:** done  

**Dependencies:** None  


애플리케이션 계층에서 인프라 의존성을 제거하고 캐시 로직을 분리합니다.

### 2.3. 계층 간 DTO 강결합 해소 및 매퍼 구조화

**Status:** pending  

**Dependencies:** None  


계층 간 DTO 결합을 완화하고 매퍼 구조를 명확히 분리합니다.

### 2.4. Presentation 종속적인 Facade(Support)를 Application 레이어로 승격(이동)

**Status:** pending  

**Dependencies:** None  


Presentation 종속 Support/Façade 성격의 로직을 Application 레이어로 이동합니다.

### 2.5. RestClient 예외 처리 횡단 관심사 중앙화 및 예외 변환 적용

**Status:** pending  

**Dependencies:** None  


RestClient 호출 예외 처리와 예외 변환 로직을 공통화합니다.

### 2.6. Global Exception Handler (@ControllerAdvice) 및 API 규격 표준화 적용

**Status:** pending  

**Dependencies:** None  


전역 예외 처리와 응답 규격 표준화를 적용합니다.

### 2.7. Outbound Port 설계의 객체지향적 세분화 (단일 책임, 인터페이스 분리 원칙 위배 해소)

**Status:** pending  

**Dependencies:** None  


Outbound Port를 책임 단위로 세분화해 인터페이스 분리 원칙을 반영합니다.

### 2.8. 리팩토링 회귀 테스트 보강 (핵심 컴포넌트 단위/통합 테스트)

**Status:** pending  

**Dependencies:** None  


리팩토링 영향 구간에 대한 단위/통합 회귀 테스트를 보강합니다.

### 2.9. 아키텍처 경계 검증 자동화 (계층 의존 규칙 테스트)

**Status:** pending  

**Dependencies:** None  


계층 간 의존 규칙을 자동 검증해 아키텍처 경계 위반 재발을 방지합니다.

### 2.10. 대시보드 조회 성능 리팩토링 (커밋 조회 N+1 완화)

**Status:** pending  

**Dependencies:** None  


대시보드 조회 시 커밋 조회 N+1 패턴을 완화해 성능을 개선합니다.
