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

### 1.2. [2.3] Find a file용 전체 파일 인덱스 캐시(Valkey) 적용

**Status:** done  

**Dependencies:** 1.1  


브랜치 기준 전체 파일 목록 인덱스를 캐시해 Find a file 키업 검색 응답을 실시간으로 제공한다.

**Details:**


RepositoryDetailService에서 branch HEAD(commit SHA) 기반 키로 전체 파일 인덱스를 캐시(5분 TTL)하고, /{namespace}/{repoName}/find-files 엔드포인트에서 키워드 필터 결과를 반환한다.

### 1.3. [2.4] Repository 화면 기능 버튼 인증/인가 제어

**Status:** pending  

**Dependencies:** 1.1  


Public repository 조회는 허용하되 쓰기성 기능은 인증/권한 사용자로 제한한다.

**Details:**


브랜치 생성/파일 업로드/디렉터리 생성 버튼 노출 및 서버 호출 권한을 일관되게 통제한다.
