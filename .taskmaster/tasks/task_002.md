# Task ID: 2

**Title:** Repository Feature 확장

**Status:** in-progress

**Dependencies:** None

**Priority:** high

**Description:** 저장소 사용성 향상을 위한 브랜치 생성 및 파일 업로드 기능 추가

**Details:**

Repository 상세 화면에서 사용자가 새 브랜치를 생성하고 새 파일을 업로드할 수 있는 기능을 구현한다.

**Test Strategy:**

컨트롤러/서비스 단위 테스트 및 업로드 플로우 통합 테스트

## Subtasks

### 2.1. New Branch 생성 기능

**Status:** done  
**Dependencies:** None  

사용자가 저장소에서 새 브랜치를 생성할 수 있는 UI/API 플로우 구현

**Details:**

브랜치명 검증, 중복 브랜치 처리, 생성 성공 후 브랜치 목록/선택 상태 반영

### 2.2. New File 생성 기능 (File Upload)

**Status:** done  
**Dependencies:** None  

사용자가 파일을 업로드하여 새 파일을 저장소에 생성할 수 있는 기능 구현

**Details:**

업로드 크기/확장자 검증, 경로 선택, 커밋 메시지 입력, 업로드 성공 후 트리/상세 화면 반영

### 2.3. Find a file용 브랜치별 트리 캐시 설계

**Status:** pending  
**Dependencies:** None  

jgitkins-web에서 Valkey 기반 브랜치/커밋 SHA 키 캐시로 트리 인덱스 조회 경로를 설계하고 연동 포인트를 정의한다.

**Details:**

상세 진입 시 cache hit/miss 플로우(Valkey 조회 -> miss 시 jgit-server 질의 -> 캐시 저장), commit SHA 기반 키 전략, TTL/재검증 정책, 브랜치 전환 시 동작을 문서화하고 구현 범위를 분리한다.

### 2.4. Repository 화면 기능 버튼 인증/인가 제어

**Status:** pending  
**Dependencies:** 2.1, 2.2  

Public repository는 비로그인 접근을 허용하되, 브랜치 생성/파일 업로드/수정성 기능 버튼은 인증 및 권한 보유 사용자에게만 노출·실행 가능하도록 제한한다.

**Details:**

Repository detail/tree 화면에서 쓰기 액션 UI를 권한 기반으로 분기하고, 서버 측에서도 동일 정책으로 재검증한다. 비인증/권한없음 사용자는 기능 버튼 비활성 또는 숨김 처리 및 API 호출 시 401/403 응답을 보장한다.
