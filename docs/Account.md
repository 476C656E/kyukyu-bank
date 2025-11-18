```kotlin
interface AccountRepository : JpaRepository<AccountEntity, Long> {
    // 계좌번호 채번을 위한 로직
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO account_number_sequence VALUES ()")
    fun insertAccountNumberSequence()

    @Query(nativeQuery = true, value = "SELECT LAST_INSERT_ID()")
    fun getLastInsertId(): Long

    // 계좌번호 일련번호를 생성한다.
    @Transactional
    fun getNextAccountNumberSequence(): Long {
        insertAccountNumberSequence()
        return getLastInsertId()
    }
}
```
위 코드는 기능적으로 동작하나, 구현시에 논리적인 생각 흐름으로 인해 놓친 부분이 많다. 계좌 번호는 절대적으로 중복되선 안돼고, 생성 부분을 디테일 있고 고유성을 보장하려면 어떤 방법이 존재할까 고민한 코드이다.

먼저 상기 코드의 설명을 가볍게 한다.
1. getNextAccountNumberSequence()가 호출되면 @Transaction에 의해 트랜잭션이 시작된다.
2. insertAccountNumberSequence()가 호출되어 account_number_sequence 테이블에 빈 레코드를 Insert 한다. (여기서 MySQL의 DB 시퀀스 AUTO_INCREMENT를 사용해서 원자적으로 증가하는 번호 생성이 계좌번호 할당에 고유성을 보장한다.)
3. getLastInsertId()가 호출되어 SELECT LAST_INSERT_ID() 쿼리를 실행한다.
4. 트랜잭션이 커밋되고 생성된 ID를 반환한다.

여기서, Spring의 @Transaction 어노테이션은 insert, getLast 두 메서드가 동일한 DB 커넥션을 사용하도록 보장한다.
LAST_INSERT_ID() 함수는 커넥션별로 마지막 ID를 반환하기에 다른 커넥션에서 INSERT가 동시에 발생하더라도 해당 트랜잭션의 커넥션에서 생성된 ID를 정확하게 가져올 수 있다.

## 문제점
### 배타적 락에 의한 병목 지점
getNextAccountNumberSequence() 메서드가 호출될 때 마다 account_number_sequence 테이블에 배타적 락이 걸리게 된다.
여기서 항상 창서님께서 말씀하신 부분이다. -> 만약, 1000명 또는 그 이상의 n명의 사용자가 동시에 계좌 개설을 시도한다면?
맞다... 해당 n개의 스레드가 이 메서드를 호출하는데, 한 번에 하나의 스레드만 락을 얻어 INSERT를 수행할 수 있다.
즉, n-1명의 사용자는 앞 사용자의 트랜잭션이 끝날 때 까지 순차적 대기를 해야하며, 동시 사용자가 조금만 늘어나도 시스템 전체 응답 시간을 급격히 저하시키는 심각한 병목 지점이다.

### DB 종속성
먼저, LAST_INSERT_ID()는 MySQL 전용 함수인데, 다른 RDB를 사용할 경우 해당 코드는 동작하지 않는다.
즉, 재사용성이 떨어지는 코드이다. (아니 근데 네이티브 쿼리 쓸 수 밖에 없으면? -> 금융권에서 이런 부분은 Oracle만 쓴다고 착각)

정리하자면, JPA는 특정 데이터베이스에 종속되지 않기 위함인데, nativeQuery = true로 인해 트레이드오프 해야한다.
-> JPA에서 nativeQuery 언제 사용해야할까? JPA로 해결 하지 못해 쿼리 성능을 올려야할 때? *궁금

### 관심사 분리
Repository는 Entity에 대한 데이터 접근(CRUD) 이다.
getNextAccountNumberSequence() 함수의 역할은 "계좌번호를 생성"인데, 비즈니스 로직에 가깝다.
이런 로직이 데이터 접근 인터페이스 내부에 default 메서드로 구현되어 있으면 책임이 모호해지고 유지보수가 어려워진다.


아기 치와와 전재규는 이렇게 해결한다.

알아본 봐 JPA는 "테이블을 이용한 시퀀스 생성"을 표준 기능으로 제공한다고 한다.
@TableGenerator 어노테이션인데, JPA에서 PK를 자동으로 생성하기 위해 별도의 테이블을 사용하는 전략이라고 한다.
음, 별도의 테이블을 사용하는 전략? 감이 잘 오지않는다. 쉽게 생각해보면 시퀀스를 활용해서 생성된 계좌 번호만 따로 모아둔 테이블 같은 느낌인데, 테스트를 해봤다.

하기 코드는 기존 AccountEntity 코드이다.
```kotlin
@Comment("회원 계좌")
@Entity
@Table(name = "account")
class AccountEntity(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:Comment("유저 번호")
    @field:Column(name = "fk_account_user")
    val userId: Long,

    @field:Comment("계좌번호")
    @field:Column(name = "account_number", unique = true, nullable = false)
    val accountNumber: String,

    @field:Comment("계좌 비밀번호")
    @field:Column(name = "account_password", nullable = false)
    val accountPassword: String,

    @field:Comment("계좌 잔액")
    @field:Column(name = "account_balance", nullable = false)
    var balance: BigDecimal,

    @field:Comment("은행 코드")
    @field:Column(name = "account_bank_code", nullable = false)
    val bankCode: String,

    @field:Comment("계좌 유형")
    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "account_type", nullable = false)
    val type: AccountType,

    @field:Comment("계좌 통화")
    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "account_currency", nullable = false)
    val currency: AccountCurrency,

    @field:Comment("계좌 상태")
    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "account_status", nullable = false)
    var status: AccountStatus,
) {
    @CreationTimestamp
    @Comment("계좌 개설 일시")
    @Column(name = "account_opened_at", nullable = false, updatable = false)
    lateinit var openedAt: LocalDateTime
        private set

    @UpdateTimestamp
    @Comment("수정 일시")
    @Column(name = "updated_at", nullable = false)
    lateinit var updatedAt: LocalDateTime
        private set
}
```

요게~

```kotlin
@Comment("회원 계좌")
@Entity
@Table(name = "account")
@TableGenerator(
    name = "ACCOUNT_ID_GENERATOR",
    table = "id_sequences",
    pkColumnName = "sequence_name",
    valueColumnName = "next_val",
    pkColumnValue = "account_seq",
    initialValue = 1,
    allocationSize = 1
)
class AccountEntity(
    @field:Id
    @field:GeneratedValue(
        strategy = GenerationType.TABLE, 
        generator = "ACCOUNT_ID_GENERATOR"
    )
    val id: Long = 0,

    @field:Comment("유저 번호")
    @field:Column(name = "fk_account_user")
    val userId: Long,

    @field:Comment("계좌번호")
    @field:Column(name = "account_number", unique = true, nullable = false)
    val accountNumber: String,

    @field:Comment("계좌 비밀번호")
    @field:Column(name = "account_password", nullable = false)
    val accountPassword: String,

    @field:Comment("계좌 잔액")
    @field:Column(name = "account_balance", nullable = false)
    var balance: BigDecimal,

    @field:Comment("은행 코드")
    @field:Column(name = "account_bank_code", nullable = false)
    val bankCode: String,

    @field:Comment("계좌 유형")
    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "account_type", nullable = false)
    val type: AccountType,

    @field:Comment("계좌 통화")
    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "account_currency", nullable = false)
    val currency: AccountCurrency,

    @field:Comment("계좌 상태")
    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "account_status", nullable = false)
    var status: AccountStatus,
) {
    @CreationTimestamp
    @Comment("계좌 개설 일시")
    @Column(name = "account_opened_at", nullable = false, updatable = false)
    lateinit var openedAt: LocalDateTime
        private set

    @UpdateTimestamp
    @Comment("수정 일시")
    @Column(name = "updated_at", nullable = false)
    lateinit var updatedAt: LocalDateTime
        private set
}
```

우선, IDENTITY는 DB의 AUTO_INCREMENT를 사용하지만, TABLE은 별도 테이블을 사용하여 ID를 관리한다.

```sql
CREATE TABLE id_sequences (
    sequence_name VARCHAR(255) NOT NULL,
    next_val BIGINT,
    PRIMARY KEY (sequence_name)
);
```

실제로 어떻게 동작하나면,

```sql
-- 1. 현재 시퀀스 값 조회 (락 획득)
SELECT next_val FROM id_sequences 
WHERE sequence_name = 'account_seq' FOR UPDATE;

-- 2. 시퀀스 값 업데이트
UPDATE id_sequences 
SET next_val = ? 
WHERE sequence_name = 'account_seq';

-- 3. 계좌 엔티티 INSERT
INSERT INTO account (...) VALUES (?);
```
@TableGenerator를 사용하면 메모리에 ID를 미리 할당받아 DB 접근 횟수를 줄일 수 있다고 한다.
여전히 감이 안오네...

SequenceGenerator도 존재하는데 핵심 차이로서, 이름 그대로 DB의 Sequence 객체를 직접 사용한다.
Table은 별도의 테이블을 생성해 시퀀스 흉내를 낸다. (!?)

하지만 TableGenerator에 비해 지원되는 DB는 Oracle, PostgreSQL, H2등 뿐이라고 한다. (이 정도면 훌륭한거아니야?)
쿼리 방식은 SELECT next_val이 1회다. (Table은 SELECT + UPDATE 2회)
성능은 당연히 시퀀스가 빠르고, 그만큼 DB 의존성도 높다.

