# Transaction Entity 사용 가이드

## 개요
이중 기입(Double-Entry Bookkeeping) 방식으로 리팩토링된 TransactionEntity 사용 가이드입니다.

## 핵심 개념

### 1. 이중 기입 (Double-Entry)
모든 거래는 **차변(DEBIT)**과 **대변(CREDIT)**으로 기록됩니다.

- **DEBIT (차변)**: 출금 (자산 감소)
- **CREDIT (대변)**: 입금 (자산 증가)

### 2. 거래 그룹 (Transaction Group)
내부 송금의 경우, 출금과 입금이 쌍을 이루므로 같은 `transactionGroupId`로 묶입니다.

## 사용 예시

### 1. 내부 송금 (A 계좌 → B 계좌, 10,000원)

```kotlin
val groupId = UUID.randomUUID().toString()

// ① A 계좌 출금 (차변)
val debitTransaction = TransactionEntity(
    transactionGroupId = groupId,
    accountId = accountA.id,
    entryType = EntryType.DEBIT,
    amount = BigDecimal("10000"),
    transferCategory = TransferCategory.INTERNAL,
    status = TransactionStatus.SUCCESS,
    counterAccountId = accountB.id,  // 상대방 계좌 ID
    balanceBefore = BigDecimal("50000"),
    balanceAfter = BigDecimal("40000"),
    memo = "B에게 송금"
)

// ② B 계좌 입금 (대변)
val creditTransaction = TransactionEntity(
    transactionGroupId = groupId,  // 같은 그룹 ID
    accountId = accountB.id,
    entryType = EntryType.CREDIT,
    amount = BigDecimal("10000"),
    transferCategory = TransferCategory.INTERNAL,
    status = TransactionStatus.SUCCESS,
    counterAccountId = accountA.id,  // 상대방 계좌 ID
    balanceBefore = BigDecimal("30000"),
    balanceAfter = BigDecimal("40000"),
    memo = "A로부터 입금"
)

transactionRepository.saveAll(listOf(debitTransaction, creditTransaction))
```

### 2. 외부 송금 (A 계좌 → 타행 계좌, 20,000원)

```kotlin
val groupId = UUID.randomUUID().toString()

val externalTransfer = TransactionEntity(
    transactionGroupId = groupId,
    accountId = accountA.id,
    entryType = EntryType.DEBIT,
    amount = BigDecimal("20000"),
    transferCategory = TransferCategory.EXTERNAL,
    status = TransactionStatus.SUCCESS,
    counterBankCode = "004",  // 국민은행
    counterAccountNumber = "123-456-789012",
    balanceBefore = BigDecimal("50000"),
    balanceAfter = BigDecimal("30000"),
    memo = "타행 송금"
)

transactionRepository.save(externalTransfer)
```

### 3. 현금 입금 (ATM 등)

```kotlin
val groupId = UUID.randomUUID().toString()

val depositTransaction = TransactionEntity(
    transactionGroupId = groupId,
    accountId = accountA.id,
    entryType = EntryType.CREDIT,
    amount = BigDecimal("50000"),
    transferCategory = TransferCategory.DEPOSIT,
    status = TransactionStatus.SUCCESS,
    balanceBefore = BigDecimal("100000"),
    balanceAfter = BigDecimal("150000"),
    memo = "현금 입금"
)

transactionRepository.save(depositTransaction)
```

### 4. 현금 출금 (ATM 등)

```kotlin
val groupId = UUID.randomUUID().toString()

val withdrawalTransaction = TransactionEntity(
    transactionGroupId = groupId,
    accountId = accountA.id,
    entryType = EntryType.DEBIT,
    amount = BigDecimal("30000"),
    transferCategory = TransferCategory.WITHDRAWAL,
    status = TransactionStatus.SUCCESS,
    balanceBefore = BigDecimal("150000"),
    balanceAfter = BigDecimal("120000"),
    memo = "ATM 출금"
)

transactionRepository.save(withdrawalTransaction)
```

### 5. 실패한 거래

```kotlin
val groupId = UUID.randomUUID().toString()

val failedTransaction = TransactionEntity(
    transactionGroupId = groupId,
    accountId = accountA.id,
    entryType = EntryType.DEBIT,
    amount = BigDecimal("100000"),
    transferCategory = TransferCategory.EXTERNAL,
    status = TransactionStatus.FAILED,
    counterBankCode = "011",
    counterAccountNumber = "987-654-321098",
    balanceBefore = BigDecimal("50000"),
    balanceAfter = BigDecimal("50000"),  // 실패 시 잔액 변화 없음
    failureReason = "잔액 부족",
    memo = "송금 실패"
)

transactionRepository.save(failedTransaction)
```

## 조회 쿼리 예시

### 특정 계좌의 모든 거래 내역
```kotlin
fun findByAccountId(accountId: Long, pageable: Pageable): Page<TransactionEntity>
```

### 특정 거래 그룹 조회 (내부 송금의 쌍)
```kotlin
fun findByTransactionGroupId(transactionGroupId: String): List<TransactionEntity>
```

### 날짜 범위로 조회
```kotlin
fun findByAccountIdAndTransactionDateBetween(
    accountId: Long,
    startDate: LocalDateTime,
    endDate: LocalDateTime,
    pageable: Pageable
): Page<TransactionEntity>
```

### 송금 구분별 조회
```kotlin
fun findByAccountIdAndTransferCategory(
    accountId: Long,
    category: TransferCategory,
    pageable: Pageable
): Page<TransactionEntity>
```

## 주요 변경 사항

1. **이중 기입 지원**: `EntryType` (DEBIT/CREDIT) 추가
2. **거래 그룹**: `transactionGroupId`로 관련 거래 연결
3. **송금 구분**: `TransferCategory`로 내부/외부/입금/출금 구분
4. **상대방 정보**:
   - 내부 송금: `counterAccountId`
   - 외부 송금: `counterBankCode`, `counterAccountNumber`
5. **잔액 스냅샷**: `balanceBefore`, `balanceAfter`
6. **감사 기능**: `failureReason`, `memo`
7. **인덱스 추가**: 조회 성능 최적화
8. **불변성**: 주요 필드에 `updatable = false`

## 주의사항

1. **내부 송금은 반드시 2개의 레코드를 생성**해야 합니다 (출금 + 입금)
2. **transactionGroupId는 UUID 등 유일한 값**을 사용하세요
3. **잔액 스냅샷은 정확해야** 감사 추적이 가능합니다
4. **실패한 거래도 기록**하여 감사 추적을 남기세요
5. **거래는 수정 불가능**합니다 (updatable = false)
