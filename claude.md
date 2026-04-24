     Context

     10분/10루프 구조의 뱀서식(Vampire Survivors-like) 액션 게임을 Java로 신규 개발한다.
     핵심 차별점은 사망 후 재시도 시 직전 루프의 행동이 잔상(영웅)으로 복제되어 누적된다는 점이다.
     기존 뱀서식의 "영웅 수집/성장"의 재미를 과거의 내 플레이 자체를 수집·강화하는 메커니즘으로 대체한다.

     아직 기존 코드베이스가 없는 그린필드 프로젝트이며, 본 플랜은 MVP까지의 아키텍처와 구현 순서를 정의한다.

     ---
     확정된 설계 결정

     ┌───────────────────┬─────────────────────────────────────────────────────────────────────────┐
     │       항목        │                                  결정                                   │
     ├───────────────────┼─────────────────────────────────────────────────────────────────────────┤
     │ 기술 스택         │ LibGDX (gdx-setup로 초기화)                                             │
     ├───────────────────┼─────────────────────────────────────────────────────────────────────────┤
     │ 게임 길이         │ 총 10분, 최대 10루프                                                    │
     ├───────────────────┼─────────────────────────────────────────────────────────────────────────┤
     │ 루프 종료 조건    │ 사망 시에만 다음 루프 시작                                                 │
     ├───────────────────┼─────────────────────────────────────────────────────────────────────────┤
     │ 직업 선택        │ 각 루프 시작 시 플레이어가 선택 (칼잡이/궁수/버퍼), 추가가 쉽도록 코드 작성         │
     ├───────────────────┼─────────────────────────────────────────────────────────────────────────┤
     │ 무기 선택          │ 몬스터 처치 시 일정 확률로 드랍되는 구슬 획득 시, 레벨업과 동시에 무기선택 or 기존 무기강화│
     ├───────────────────┼─────────────────────────────────────────────────────────────────────────┤
     │ 잔상 복제 방식      │ 직전 루프의 입력을 상대 좌표(플레이어 시작점 기준 오프셋) 로 저장·재생  │
     ├───────────────────┼─────────────────────────────────────────────────────────────────────────┤
     │ 재생 종료 후 잔상   │ 제자리 고정, 근거리 공격만 수행 (명령 스킬은 MVP 이후)                  │
     ├───────────────────┼─────────────────────────────────────────────────────────────────────────┤
     │ 시각 표현         │ 잔상 반투명, 오래된 루프일수록 더 투명. 현재 플레이어는 불투명 + 외곽선 │
     ├───────────────────┼─────────────────────────────────────────────────────────────────────────┤
     │ 무적 처리          │ 잔상은 완전 무적, 적은 잔상을 타겟팅하지 않음 (현재 플레이어만 추적)    │
     ├───────────────────┼─────────────────────────────────────────────────────────────────────────┤
     │ 예측 UI           │ 없음 — 플레이어 기억 의존                                               │
     ├───────────────────┼─────────────────────────────────────────────────────────────────────────┤
     │ MVP 시스템         │ 포메이션 시너지 + 영구 업그레이드 트리                                  │
     └───────────────────┴─────────────────────────────────────────────────────────────────────────┘

     ---
     기술 스택 상세

     - 엔진: LibGDX 1.12+
     - 빌드: Gradle (LibGDX 공식 setup 툴 사용)
     - 타겟 플랫폼: Desktop (LWJGL3 백엔드)
     - 렌더링: SpriteBatch (2D)
     - 입력: InputProcessor + polling 혼합
     - 직렬화: LibGDX Json (영구 업그레이드 상태 저장용)

     ---
     프로젝트 구조

     프로젝트 루트는 사용자가 지정(예: C:\Users\Lime\projects\loop-survivors).
     LibGDX 표준 레이아웃을 따르며 core/src/ 하위에 아래 패키지를 둔다.

     core/src/com/loopsurvivors/
     ├── LoopSurvivorsGame.java        // ApplicationListener 진입점
     ├── screen/
     │   ├── MenuScreen.java           // 시작/직업 선택
     │   ├── GameScreen.java           // 인게임 (루프 반복)
     │   └── UpgradeScreen.java        // 영구 업그레이드 트리
     ├── world/
     │   ├── World.java                // 엔티티 컨테이너, 틱 루프
     │   ├── Player.java               // 현재 조작 중인 캐릭터
     │   ├── Ghost.java                // 재생 중인 과거 자아
     │   ├── Enemy.java
     │   └── Projectile.java
     ├── loop/
     │   ├── InputFrame.java           // 한 틱의 입력 스냅샷
     │   ├── LoopRecording.java        // 루프 1개의 InputFrame 시퀀스
     │   ├── LoopRecorder.java         // 현재 플레이 기록
     │   └── GhostReplayer.java        // LoopRecording → Ghost 행동 변환
     ├── combat/
     │   ├── ClassType.java            // SWORD / BOW / HEAL (enum)
     │   ├── Weapon.java               // 공격 패턴
     │   └── FormationSynergy.java     // 잔상 구성 → 보너스 계산
     ├── progression/
     │   ├── SoulCurrency.java         // 직업별 소울 잔액
     │   ├── UpgradeNode.java          // 트리 노드 정의
     │   └── UpgradeTree.java          // 해금 상태 + 영구 저장
     ├── spawn/
     │   └── WaveDirector.java         // 뱀서식 적 웨이브 스케일링
     └── render/
         ├── Renderer.java             // SpriteBatch 관리
         └── GhostRenderer.java        // 반투명/색조/외곽선 처리

     ---
     핵심 아키텍처

     1. 틱 기반 결정론적 시뮬레이션

     60Hz 고정 타임스텝. Gdx.graphics.getDeltaTime() 누적 → 16.67ms 단위로 world.tick() 실행.
     렌더링은 별개로 진행하되, 시뮬레이션은 결정론적이어야 잔상 재생이 정확히 맞는다.

     2. 입력 기록 & 재생

     - InputFrame { tickIndex, moveDir(vec2), attackPressed, skillPressed, ... } 구조체.
     - LoopRecorder는 매 틱 현재 입력을 LoopRecording에 append.
     - 좌표는 플레이어의 루프 시작 위치 기준 오프셋으로 기록 (이동 방향 벡터만 저장하므로 자연스럽게 상대적).
     - 사망 → LoopRecording 확정 → 다음 루프 시작 시 Ghost에 주입.
     - GhostReplayer는 매 틱 recording.get(tickIndex)를 읽어 Ghost의 이동·공격 수행.
     - tickIndex >= recording.size() 도달 시 정지 모드로 전환 — 이동 중지, 사거리 내 적에게만 공격.

     3. Ghost 무적 & 적 AI

     - Ghost는 invincible = true. 충돌 체크에서 피격 스킵.
     - Enemy AI의 타겟 탐색은 world.getPlayer()만 참조. Ghost는 타겟 후보에서 제외.
     - Ghost의 공격은 적에게 정상 적용 (단방향 데미지).

     4. 직업/무기 시스템

     ClassType enum — SWORD, BOW, HEAL. 각 루프 시작 시 선택 UI 제공.

     ┌───────┬────────────────────────┬────────────────────────────────────────────────────────────┐
     │ Class │          공격          │                            특징                            │
     ├───────┼────────────────────────┼────────────────────────────────────────────────────────────┤
     │ SWORD │ 근접 스윙 (정면 원호)     │ 높은 DPS, 위험 노출                                        │
     ├───────┼────────────────────────┼────────────────────────────────────────────────────────────┤
     │ BOW   │ 원거리 투사체             │ 안전거리, 후열 담당                                        │
     ├───────┼────────────────────────┼────────────────────────────────────────────────────────────┤
     │ HEAL  │ AoE 힐링 + 약한 원거리 │ 다른 잔상 생존 기여(무적이므로 MVP에선 버프 제공으로 대체) │
     └───────┴────────────────────────┴────────────────────────────────────────────────────────────┘

     ※ 잔상이 무적이므로 HEAL은 MVP에서 주변 아군 공격력 버프 오라로 기능 재정의.

     5. 포메이션 시너지

     잔상 구성에 따라 현재 플레이어에게 보너스 적용.

     - 전열 방패진 (SWORD 잔상 3+): 현재 플레이어 피격 시 10% 확률 무시
     - 집중 사격 (BOW 잔상 3+): 투사체 관통 +1
     - 지원 진영 (HEAL 잔상 2+): 체력 재생 +0.5/s
     - 균형 편대 (각 직업 1 이상 동시): 모든 데미지 +15%

     FormationSynergy.evaluate(List<Ghost>) → BonusSet 반환, 매 루프 시작 시 1회 계산.

     6. 영구 업그레이드 트리

     - 루프 종료 시 처치 수·생존 시간 기반 소울 지급 (직업별 분리).
     - 메인 메뉴의 UpgradeScreen에서 소울 소모하여 노드 해금.
     - 트리 구조: 직업별 3개 브랜치(공격/방어/유틸), 각 5단계.
     - 해금 상태는 ~/.loopsurvivors/progress.json에 LibGDX Json으로 저장.
     - 게임 시작 시 UpgradeTree.loadAndApply() 호출하여 스탯/능력 반영.

     7. 렌더링: 잔상 시각화

     float alpha = 0.3f + 0.5f * (ghost.loopIndex / (float)currentLoop);
     batch.setColor(classTint(ghost.classType), alpha);
     batch.draw(ghost.sprite, ...);

     - 오래된 루프일수록 더 투명(0.3 근방), 최근 루프는 선명(0.8 근방).
     - 현재 플레이어는 alpha=1.0 + shader로 외곽선 스트로크.
     - 직업별 색조: SWORD=붉은 계열, BOW=녹색 계열, HEAL=푸른 계열.

     8. 적 웨이브

     WaveDirector — 경과 시간 + 현재 루프 번호에 비례하여 스폰 레이트/체력 스케일링.
     루프 누적 공격력을 상쇄하기 위해 루프당 적 HP 1.3× 증가.

     ---
     구현 순서 (MVP → 확장)

     단계 1 — 기반 (1~2일)

     1. LibGDX gdx-setup으로 프로젝트 생성
     2. LoopSurvivorsGame + GameScreen 최소 구현: 플레이어 이동/근접 공격
     3. 고정 타임스텝 틱 루프 구성
     4. 더미 적 스폰 + 피격/사망 처리

     단계 2 — 루프 & 잔상 (2~3일) ← 핵심

     5. InputFrame/LoopRecording/LoopRecorder 구현
     6. 사망 감지 → 녹화 확정 → 다음 루프 시작 시 Ghost 생성
     7. GhostReplayer로 잔상 행동 재현 (이동 + 공격)
     8. 재생 종료 후 정지 모드 전환
     9. 잔상 무적 + 적 타겟팅 제외 로직
     10. 반투명 렌더링 + 현재 플레이어 외곽선

     단계 3 — 직업/전투 확장 (2일)

     11. ClassType 3종 + 루프 시작 시 선택 UI
     12. 각 직업별 Weapon 구현
     13. WaveDirector 스케일링 튜닝

     단계 4 — MVP 시스템 (2일)

     14. FormationSynergy 계산 + 적용
     15. 루프 종료 시 소울 지급
     16. UpgradeScreen + UpgradeTree 해금 UI
     17. Json 저장/로드

     단계 5 — 후속 (MVP 이후)

     - 명령 시스템 (집결/거점/돌격)
     - 루프 카드 덱빌딩
     - 진보스 "그림자 자아"
     - 운명 모디파이어

     ---
     검증 방법

     핵심 동작 확인

     1. ./gradlew desktop:run으로 실행
     2. 1루프: 이동 + 공격으로 적 처치, 사망까지 플레이
     3. 2루프 시작 시 직전 경로/공격을 그대로 반복하는 반투명 잔상 1명 확인
     4. 잔상이 적 공격을 받아도 HP 감소 없음을 확인 (무적)
     5. 적이 잔상을 스킵하고 현재 플레이어만 추적하는지 확인
     6. 녹화 길이 초과 후 잔상이 제자리에 멈추고 근거리 적만 공격하는지 확인

     시스템 확인

     7. 3루프째에서 직업 선택 UI 정상 출현
     8. 칼잡이 잔상 3명 구성 후 "전열 방패진" 보너스가 실제 피격 무시로 이어지는지 로그 확인
     9. 루프 종료 시 소울 적립, 메뉴에서 업그레이드 해금 후 다음 게임에 반영 확인
     10. 프로그램 재시작 후에도 업그레이드 상태가 progress.json에서 복원되는지 확인

     결정론 검증

     11. 동일한 입력 시퀀스를 반복 재생했을 때 잔상이 매번 같은 위치/타이밍에 공격하는지 확인 (고정 타임스텝 무결성)





--- 이미지 생성용

 ---
  SWORD (칼잡이)
  2D game character sprite sheet, pixel art, side view,
  warrior with sword and red armor,
  1 row 4 columns, 64x64 per frame, walk cycle 4 frames,
  character facing right, legs and arms moving,
  transparent background, dark fantasy style

  BOW (궁수)
  2D game character sprite sheet, pixel art, side view,
  archer with bow and green cloak,
  1 row 4 columns, 64x64 per frame, walk cycle 4 frames,
  character facing right, legs and arms moving,
  transparent background, dark fantasy style

  HEAL (버퍼)
  2D game character sprite sheet, pixel art, side view,
  healer with staff and blue robe, glowing aura,
  1 row 4 columns, 64x64 per frame, walk cycle 4 frames,
  character facing right, legs and arms moving,
  transparent background, dark fantasy style


  JAVA_HOME="C:/Program Files/Java/jdk-17" ./gradlew desktop:run