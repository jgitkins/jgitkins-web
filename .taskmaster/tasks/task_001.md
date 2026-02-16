# Task ID: 1

**Title:** 신규기능

**Status:** in-progress

**Dependencies:** None

**Priority:** high

**Description:** 사용자 가치 전달을 위한 기능 개발/확장 작업

**Details:**


카테고리 기반 상위 Task

**Test Strategy:**


하위 작업별 회귀 테스트

## Subtasks

### 1.1. [2] Repository Feature 확장

**Status:** in-progress  

**Dependencies:** None  


저장소 사용성 향상을 위한 브랜치 생성 및 파일 업로드 기능 추가

**Details:**


Repository 상세 화면에서 사용자가 새 브랜치를 생성하고 새 파일을 업로드할 수 있는 기능을 구현한다.


(legacy task: 2)

### 1.2. [2.3] 저장소 트리 서빙 캐시(Valkey) 적용

**Status:** done  

**Dependencies:** 1.1  


브랜치 HEAD(commit SHA) + 디렉터리 기반으로 트리 조회를 캐싱해 find/tree 조회 latency를 줄인다.

**Details:**


RepositoryDetailService에서 tree 조회 시 commit SHA 키로 캐시 hit/miss를 분기하고 miss 시 서버 조회 후 TTL(5분)로 저장한다.

### 1.3. [2.4] Repository 화면 기능 버튼 인증/인가 제어

**Status:** pending  

**Dependencies:** 1.1  


Public repository 조회는 허용하되 쓰기성 기능은 인증/권한 사용자로 제한한다.

**Details:**


브랜치 생성/파일 업로드/디렉터리 생성 버튼 노출 및 서버 호출 권한을 일관되게 통제한다.
