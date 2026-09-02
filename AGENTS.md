# Repository Guidelines

## 프로젝트 구조 및 모듈 구성

- `app/`: Android 애플리케이션과 Jetpack Compose UI를 포함합니다. 화면별 코드는 `app/src/main/java/com/dimje/zeroclock/screen/`, 테마는 `ui/theme/`, 리소스는 `app/src/main/res/`에 둡니다.
- `domain/`: 플랫폼에 독립적인 모델, 유스케이스, Repository 인터페이스를 둡니다. Android API나 `app`, `data` 모듈에 의존하지 않습니다.
- `data/`: Repository 구현, Local/Remote DataSource, Room, Retrofit, DTO와 매퍼를 둡니다. `domain`에만 의존합니다.
- 단위 테스트는 각 모듈의 `src/test/`, 기기 테스트는 `src/androidTest/`에 작성합니다. 의존성 버전은 `gradle/libs.versions.toml`에서 중앙 관리합니다.

## 빌드, 테스트 및 개발 명령

Windows에서는 저장소 루트에서 Gradle Wrapper를 사용합니다.

- `./gradlew.bat assembleDebug`: 디버그 APK를 빌드합니다.
- `./gradlew.bat test`: 모든 모듈의 로컬 단위 테스트를 실행합니다.
- `./gradlew.bat connectedAndroidTest`: 연결된 기기나 에뮬레이터에서 계측 테스트를 실행합니다.
- `./gradlew.bat lint`: Android 정적 분석을 수행합니다.
- `./gradlew.bat clean`: 생성된 빌드 산출물을 정리합니다.

## 아키텍처 및 코딩 규칙

Kotlin 표준 스타일과 4칸 들여쓰기를 따릅니다. 클래스와 Composable은 `PascalCase`, 함수와 변수는 `camelCase`, 상수는 `UPPER_SNAKE_CASE`로 명명합니다. 모든 설명과 코드 주석은 한국어로 작성합니다.

Clean Architecture와 MVI를 유지합니다. 화면별 상태는 불변 `data class *UiState`, 입력은 `sealed interface *UiIntent`, 일회성 이벤트는 `sealed interface *UiEffect`로 정의합니다. 상태는 ViewModel에서만 `copy()`로 변경하고 Effect는 `SharedFlow` 또는 `Channel`로 전달합니다. ViewModel은 `Activity`나 `Context`를 참조하지 않습니다.

데이터는 `Compose UI → ViewModel → UseCase → Domain Repository → Data RepositoryImpl → Local/Remote DataSource → Room DAO/Retrofit Service` 순서로 전달합니다. ViewModel은 UseCase만 의존하며 Repository나 DataSource를 직접 호출하지 않습니다. `app`은 앱 진입점과 Hilt 구성 루트로서 `domain`, `data`를 조립합니다.

`domain/repository/`의 인터페이스는 `*Repository`, `data/repository/`의 구현체는 `*RepositoryImpl`로 명명합니다. DataSource 인터페이스는 `*DataSource`, 기술별 구현체는 `Room*DataSource`, `Supabase*DataSource` 형식을 사용합니다. 원격 요청과 DTO는 `remote/model/`, Entity와 Domain 변환은 `mapper/`에 둡니다. 주요 최상위 타입은 같은 이름의 파일에 하나씩 작성하고, Hilt의 `@Binds`와 `@Provides` 모듈은 역할별 파일로 분리합니다.

Composable은 State와 Intent 콜백을 받는 stateless 구조로 작성하고 비즈니스 로직을 포함하지 않습니다. 모든 UI 컴포넌트에 더미 데이터를 사용한 `@Preview`를 제공하며, 컬렉션에는 필요에 따라 `ImmutableList`, `@Immutable`, `@Stable`을 적용합니다. 의존성 주입은 Hilt를 사용합니다.

## 테스트 지침

로컬 로직은 JUnit4, Android 통합 동작은 AndroidX Test와 Espresso, Compose UI는 Compose Test로 검증합니다. 테스트 클래스는 대상 이름에 `Test`를 붙이고(예: `HomeViewModelTest`), 테스트 함수명은 기대 동작이 드러나게 작성합니다. 기능 변경에는 정상 경로와 주요 실패 경로 테스트를 함께 추가합니다.

## 커밋 및 Pull Request

커밋은 UI, 데이터 로직, 테스트처럼 한 가지 논리적 변경만 담습니다. 제목은 `<type> : <한국어 설명>` 형식을 사용하며, 예시는 `feat : 고민 상세 화면 추가`입니다. `type`은 `feat`, `fix`, `refactor`, `test`, `docs`, `chore` 중 변경 목적에 맞게 선택합니다. 본문에는 `포함 범위` 제목 아래 주요 변경사항을 한국어 목록으로 작성합니다. 커밋과 push는 사용자에게 메시지를 제안하고 확인받은 뒤 진행합니다.

PR에는 변경 목적, 영향 모듈, 실행한 테스트와 관련 이슈를 기록합니다. UI 변경에는 전후 스크린샷 또는 영상을 첨부합니다. 오류 원인이 불명확하면 추측으로 수정하지 말고 재현 절차와 로그를 먼저 확보합니다.
