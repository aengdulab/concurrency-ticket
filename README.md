# 미션 - 우주여행 티켓 재고 관리 시스템 설계

## 🔍 진행 방식

- 미션은 **시스템 요구 사항, 프로그래밍 요구 사항** 두 가지로 구성되어 있다.
- 두 개의 요구 사항을 만족하기 위해 노력한다. 특히 시스템을 구현하며 마주하는 문제와 해결 과정을 기록하는 데에 집중한다. 
- 시스템 요구 사항에 기재되지 않은 내용은 스스로 판단하여 구현한다.

## 📮 미션 제출 방법

- 미션 구현을 완료한 후 GitHub을 통해 제출해야 한다.
    - Repository를 fork한 이후, 미션을 구현하고 commit & push한다.
    - 미션을 완료한 이후, 본인의 Github 닉네임의 브랜치에 Pull Request를 보낸다.
    - 본인의 Github 닉네임의 브랜치는 관리자에게 요청하여 생성할 수 있다.
- 미션을 진행하면서 학습한 내용을 정리한 `docs` 폴더의 `how-to-solve.md` 파일을 작성한다.
    - 블로그, 노션 등 개인 학습 노트에 작성한 글을 복사하거나 온라인으로 게시된 글의 링크를 첨부해도 된다. 

---

## 🚀 시스템 요구 사항

**목표**: 1분 내에 매진되는 인기 있는 행성 여행 티켓 판매 시스템을 위한 티켓 재고 관리 시스템을 구현

1. **계정당 구매 가능 수량 제한**
    - 계정당 구매할 수 있는 화성(Mars)석, 금성(Venus)석 티켓을 **총 2장으로 제한**합니다.
    - 예) 금성석 2장과 화성석 2장(총 4장)은 구매 불가. 금성석 1장과 화성석 1장(총 2장)은 구매 가능.

2. **티켓 초과 판매 방지**
    - 보유하고 있는 티켓보다 **더 많은 티켓이 판매되지 않도록 재고를 철저히 관리**해야 합니다.
    - 매크로를 사용하는 부정 사용자로 인해 **하나의 계정에서 수많은 티켓 구매 요청**이 올 수 있습니다.

3. **초당 최소 1,000장의 티켓 구매 처리 성능 요구**
    - **5만 장의 티켓이 1분 내에 매진**되기 때문에, 초당 최소 **1,000장의 티켓 구매가 가능한 시스템**을 설계해야 합니다.
    - 구매에 실패한 요청(계좌 잔액 부족, 카드 비밀번호 오류 등)도 발생하기 때문에 **초당 1,000개 이상의 구매 요청이 발생**할 것을 염두에 둬야 합니다.

---

## 🎯 프로그래밍 요구 사항

- JDK 21 버전을 사용하는 것을 권장하지만 다른 버전을 사용해도 무방하다.
- README.md와 테스트 코드는 수정하지 않는다. 
- 테스트를 제외한 `build.gradle`, `docker-compose.yml` 등 모든 파일을 변경해도 된다. 
- 명확한 이유 없이는 파일, 패키지 이름을 수정하거나 이동하지 않는다.
- 코드를 깔끔하게 유지한다.

---

## 📝 힌트

미션을 진행하면서 도움이 될만한 내용을 정리한다.

### 미션 시작 - Docker 실행 방법 

미션에서 사용하는 MySQL은 Docker 컨테이너와 Docker Compose 관리되고 있다. Docker를 사용하는 방법은 다음과 같다.  
아래 명령어는 모두 Docker Compose 명령어로 docker-compose.yml이 있는 ./docker 디렉토리로 이동해서 입력해야 한다.   

- `docker-compose up -d` 명령어를 실행한다.
- `docker-compose ps` 명령어로 컨테이너가 정상적으로 실행되었는지 확인한다.
- `docker-compose down` 명령어로 컨테이너를 종료한다.
- `docker-compose down --rmi all` 명령어로 컨테이너와 이미지를 모두 삭제한다.
- MySQL 스키마(schema.sql)를 변경한 경우에는 다음을 반드시 수행해야 한다.
   - ./docker/db/mysql/data 디렉토리를 삭제한다. 
   - `docker-compose down --rmi all` 명령어로 컨테이너와 이미지를 모두 삭제한다.
   - `docker-compose up -d` 명령어로 컨테이너를 다시 실행한다.
- MySQL 컨테이너에 접속하려면 다음 명령어를 실행한다.
   - `docker-compose exec mysql mysql -u root -p` 명령어로 MySQL에 접속한다. 
   - 패스워드는 `root`이다.

### 미션 학습 키워드

문제 해결 방향성을 잡지 못할 때, 다음 키워드를 학습해 보자. 

- 트랜잭션(Transaction)
- Deadlock
- S-락(Shared Lock), X-락(Exclusive Lock)
- Java synchronized
- Spring @Transactional 프록시
- 낙관락(Optimistic Locking), 비관락(Pessimistic Locking)
- 재시도(Retry)
