package dev.kyukyubank.banking.common.utils.creator

import dev.kyukyubank.banking.common.enums.BankCode
import dev.kyukyubank.banking.common.enums.EntryType
import dev.kyukyubank.banking.common.enums.TransactionStatus
import dev.kyukyubank.banking.common.enums.TransferCategory
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.UUID
import kotlin.random.Random
import kotlin.system.measureTimeMillis
/*
*
* 맥미니 M4 Pro 24GB 기준 1억건 2분 10초, 용량 24.1기가
*
 */
object NameData {
    val LAST_NAMES = listOf(
        "김", "이", "박", "최", "정", "나", "조", "전", "장", "임",
        "한", "오", "서", "신", "권", "황", "안", "송", "류", "홍"
    )

    val FIRST_NAMES = listOf(
        "영수", "영철", "연희", "순자", "경희", "미숙", "영호", "상현", "정숙", "명자",
        "동욱", "진호", "영자", "숙희", "용호", "성민", "금자", "은정", "광수", "재호",
        "지훈", "민호", "지영", "순옥", "성훈", "민정", "미나", "은영", "동현", "혜진",
        "상우", "미경", "점례", "은주", "태현", "수경", "정훈", "미선", "병수", "경숙",
        "민수", "현우", "지현", "수빈", "재민", "민지", "승현", "은지", "정민", "혜원",
        "성진", "서연", "서준", "소영", "진우", "윤정", "도윤", "은우", "성호", "다은"
    )
}

object DataConfig {
    // 총 거래 건수 - INTERNAL은 2개씩 생성
    const val TOTAL_TRANSACTIONS = 200_000_000L
    // 배치 크기 (거래 건수 기준)
    const val BATCH_SIZE = 5_000_000L
    // 경로 (로컬: ./data/transactions.tsv, docker MySQL: /data/transactions.tsv)
    const val FILE_PATH: String = "./data/transactions.tsv"
    // 생성할 가상 계좌 수
    const val MAX_ACCOUNT_ID = 100_000L

    // 생성할 가상 은행 코드 목록 (타행, 당행)
    val BANK_CODES = listOf(
        BankCode.KYUKYU_BANK,
        BankCode.Y_BANK,
        BankCode.X_BANK,
        BankCode.Z_BANK
    ).map { it.code }

    // 금액 범위
    const val MIN_AMOUNT = 100.0
    const val MAX_AMOUNT = 1_000_000.0

    // 초기 잔액 범위
    const val MIN_INITIAL_BALANCE = 100_000.0
    const val MAX_INITIAL_BALANCE = 10_000_000.0

    // 거래 실패 확률 (0.0 ~ 1.0)
    const val FAILURE_RATE = 0.03  // 3%

    // 날짜 범위 (과거 몇 일까지)
    const val DATE_RANGE_DAYS = 365L  // 최근 1년

    // 거래 카테고리 분포
    const val INTERNAL_TRANSFER_RATE = 0.4  // 40% 내부 송금
    const val EXTERNAL_BANK_RATE = 0.3      // 30% 타행 거래
    const val DEPOSIT_RATE = 0.15           // 15% 입금
    const val WITHDRAWAL_RATE = 0.15        // 15% 출금

    // 거래 시간 차이 (출금/입금 사이의 시간 차이, 초)
    const val MIN_TRANSACTION_DELAY_SECONDS = 1L
    const val MAX_TRANSACTION_DELAY_SECONDS = 5L

    // 계좌 활동 분포 (파레토 법칙: 20%의 계좌가 80%의 거래 생성)
    const val ENABLE_PARETO_DISTRIBUTION = true
    const val ACTIVE_ACCOUNT_RATIO = 0.2  // 활발한 계좌 비율 (상위 20%)

    // 거래 실패 사유 목록
    val FAILURE_REASONS = listOf(
        "잔액 부족",
        "일일 한도 초과",
        "계좌 정지",
        "유효하지 않은 계좌",
        "네트워크 타임아웃"
    )
}

// 테스트 계좌 설정
object UserConfig {
    const val TEST_ACCOUNT_ID = 1L                      // 테스트 계좌 ID
    const val TEST_ACCOUNT_NAME = "치와와"                // 조회용 이름
    const val TEST_BANK_CODE = "1004"                   // 큐큐은행
    const val TEST_INITIAL_BALANCE = 100_000.0          // 초기 잔액 10만원
    const val TEST_ACCOUNT_NUMBER = "100000000013"      // 계좌번호
    const val TEST_ACCOUNT_TRANSACTION_RATE = 0.05      // 전체 거래의 5% (2억건 기준 1000만건)
}

// 계좌별 현재 잔액 추적 (accountId → 현재잔액)
private val accountBalances = mutableMapOf<Long, BigDecimal>()

// 계좌 잔액 가져오기 (없으면 초기화)
private fun getOrInitBalance(accountId: Long): BigDecimal {
    return accountBalances.getOrPut(accountId) {
        if (accountId == UserConfig.TEST_ACCOUNT_ID) {
            // 테스트 계좌는 고정 초기 잔액
            BigDecimal(UserConfig.TEST_INITIAL_BALANCE).setScale(2, RoundingMode.HALF_UP)
        } else {
            // 일반 계좌는 랜덤 초기 잔액
            BigDecimal(Random.nextDouble(DataConfig.MIN_INITIAL_BALANCE, DataConfig.MAX_INITIAL_BALANCE))
                .setScale(2, RoundingMode.HALF_UP)
        }
    }
}

// 계좌 잔액 업데이트
private fun updateBalance(accountId: Long, newBalance: BigDecimal) {
    accountBalances[accountId] = newBalance
}

// 랜덤 한국어 이름 생성
private fun generateRandomName(): String {
    val lastName = NameData.LAST_NAMES.random()
    val firstName = NameData.FIRST_NAMES.random()
    return "$lastName$firstName"
}

// 계좌 소유자 이름 가져오기
private fun getAccountHolderName(accountId: Long): String {
    return if (accountId == UserConfig.TEST_ACCOUNT_ID) {
        UserConfig.TEST_ACCOUNT_NAME
    } else {
        generateRandomName()
    }
}

// 랜덤 값에 따라 거래 카테고리 선택
private fun selectTransferCategory(): TransferCategory {
    val random = Random.nextDouble()
    return when {
        random < DataConfig.INTERNAL_TRANSFER_RATE -> TransferCategory.INTERNAL
        random < DataConfig.INTERNAL_TRANSFER_RATE + DataConfig.EXTERNAL_BANK_RATE -> TransferCategory.EXTERNAL
        random < DataConfig.INTERNAL_TRANSFER_RATE + DataConfig.EXTERNAL_BANK_RATE + DataConfig.DEPOSIT_RATE -> TransferCategory.DEPOSIT
        random < DataConfig.INTERNAL_TRANSFER_RATE + DataConfig.EXTERNAL_BANK_RATE + DataConfig.DEPOSIT_RATE + DataConfig.WITHDRAWAL_RATE -> TransferCategory.WITHDRAWAL
        else -> TransferCategory.WITHDRAWAL  // fallback (합이 1.0이면 도달 안함)
    }
}

// accountId → 당행 계좌번호 변환 (조회용 가짜 계좌번호)
// 예: accountId=1 → "100000000013" (치와와 특별 계좌)
// 예: accountId=1234 → "100000001234"
private fun generateAccountNumber(accountId: Long): String {
    return if (accountId == UserConfig.TEST_ACCOUNT_ID) {
        UserConfig.TEST_ACCOUNT_NUMBER  // "100000000013"
    } else {
        "100" + accountId.toString().padStart(9, '0')
    }
}

// 랜덤 계좌번호 생성 (타행 거래용)
private fun generateRandomAccountNumber(): String {
    // 12자리 랜덤 숫자 생성
    val accountNumber = (100000000000L..999999999999L).random()
    return accountNumber.toString()
}

// 파레토 분포를 적용한 계좌 ID 선택 (테스트 계정 우선)
private fun selectAccountId(maxAccountId: Long): Long {
    // 먼저 테스트 계정 확률 체크 (최우선)
    if (Random.nextDouble() < UserConfig.TEST_ACCOUNT_TRANSACTION_RATE) {
        return UserConfig.TEST_ACCOUNT_ID
    }

    // 파레토 분포 비활성화 시 균등 분포
    if (!DataConfig.ENABLE_PARETO_DISTRIBUTION) {
        return Random.nextLong(1, maxAccountId + 1)
    }

    // 파레토 법칙: 상위 20%의 계좌가 80%의 거래 생성
    val activeAccountThreshold = (maxAccountId * DataConfig.ACTIVE_ACCOUNT_RATIO).toLong()

    return if (Random.nextDouble() < 0.8) {
        // 80% 확률로 활발한 계좌 (상위 20%)
        Random.nextLong(1, activeAccountThreshold + 1)
    } else {
        // 20% 확률로 일반 계좌 (나머지 80%)
        Random.nextLong(activeAccountThreshold + 1, maxAccountId + 1)
    }
}

fun main() {
    // 설정값 로드
    val totalTransactions = DataConfig.TOTAL_TRANSACTIONS
    val batchSize = DataConfig.BATCH_SIZE
    val filePath = DataConfig.FILE_PATH
    val maxAccountId = DataConfig.MAX_ACCOUNT_ID
    val bankCodes = DataConfig.BANK_CODES

    val file = File(filePath)
    file.parentFile.mkdirs() // 디렉토리 생성

    println("TSV 파일 생성 시작...")
    println("총 $totalTransactions 건의 거래를 생성합니다. (실제 레코드는 더 많음)")

    val elapsedMillis = measureTimeMillis {
        // BufferedWriter를 사용해야 파일 쓰기 성능이 보장됩니다.
        file.bufferedWriter().use { writer ->
            var transactionCount = 0L
            var recordCount = 0L

            while (transactionCount < totalTransactions) {
                val groupId = UUID.randomUUID().toString()
                val now = LocalDateTime.now().minusDays(Random.nextLong(0, DataConfig.DATE_RANGE_DAYS + 1))
                val transferCategory = selectTransferCategory()

                // 랜덤 데이터 생성 (파레토 분포 적용)
                val accountId = selectAccountId(maxAccountId)
                var counterAccountId = selectAccountId(maxAccountId)

                // 자기 자신에게 송금 방지
                while (counterAccountId == accountId) {
                    counterAccountId = selectAccountId(maxAccountId)
                }

                val amount = BigDecimal(Random.nextDouble(DataConfig.MIN_AMOUNT, DataConfig.MAX_AMOUNT)).setScale(2, RoundingMode.HALF_UP)
                val status = if (Random.nextDouble() < DataConfig.FAILURE_RATE) TransactionStatus.FAILED else TransactionStatus.SUCCESS
                val failureReason = if (status == TransactionStatus.FAILED) DataConfig.FAILURE_REASONS.random() else "\\N"

                // 이름 생성 (계좌 ID 기반)
                val accountHolderName = getAccountHolderName(accountId)
                val counterAccountHolderName = getAccountHolderName(counterAccountId)

                when (transferCategory) {
                    TransferCategory.INTERNAL -> {
                        // 당행 송금: 출금/입금 쌍 생성 (복식부기, 1건의 거래 = 2개의 레코드)
                        val transactionDelay = Random.nextLong(DataConfig.MIN_TRANSACTION_DELAY_SECONDS, DataConfig.MAX_TRANSACTION_DELAY_SECONDS + 1)

                        // 현재 잔액 가져오기 (또는 초기화)
                        val fromBalanceBefore = getOrInitBalance(accountId)
                        val toBalanceBefore = getOrInitBalance(counterAccountId)

                        // 거래 후 잔액 계산
                        val fromBalanceAfter = fromBalanceBefore - amount
                        val toBalanceAfter = toBalanceBefore + amount

                        // 상대방 계좌번호 생성 (조회용)
                        val fromAccountNumber = generateAccountNumber(accountId)
                        val toAccountNumber = generateAccountNumber(counterAccountId)

                        // 출금(DEBIT) 레코드
                        val debit = generateTransactionRow(
                            groupId = groupId,
                            accountId = accountId,
                            entryType = EntryType.DEBIT,
                            amount = amount,
                            transferCategory = transferCategory,
                            status = status,
                            counterBankCode = UserConfig.TEST_BANK_CODE,  // "1004" (큐큐은행)
                            counterAccountNumber = toAccountNumber,       // 상대방 계좌번호
                            counterAccountId = counterAccountId,
                            balanceBefore = fromBalanceBefore,
                            balanceAfter = fromBalanceAfter,
                            failureReason = failureReason,
                            memo = "To $toAccountNumber",
                            transactionDate = now,
                            senderName = accountHolderName,
                            receiverName = counterAccountHolderName
                        )
                        writer.write(debit)
                        writer.newLine()
                        recordCount++

                        // 입금(CREDIT) 레코드
                        val credit = generateTransactionRow(
                            groupId = groupId,
                            accountId = counterAccountId,
                            entryType = EntryType.CREDIT,
                            amount = amount,
                            transferCategory = transferCategory,
                            status = status,
                            counterBankCode = UserConfig.TEST_BANK_CODE,  // "1004" (큐큐은행)
                            counterAccountNumber = fromAccountNumber,     // 상대방 계좌번호
                            counterAccountId = accountId,
                            balanceBefore = toBalanceBefore,
                            balanceAfter = toBalanceAfter,
                            failureReason = failureReason,
                            memo = "From $fromAccountNumber",
                            transactionDate = now.plusSeconds(transactionDelay),
                            senderName = accountHolderName,
                            receiverName = counterAccountHolderName
                        )
                        writer.write(credit)
                        writer.newLine()
                        recordCount++

                        // 잔액 업데이트 (성공한 거래만)
                        if (status == TransactionStatus.SUCCESS) {
                            updateBalance(accountId, fromBalanceAfter)
                            updateBalance(counterAccountId, toBalanceAfter)
                        }

                        transactionCount++  // 1건의 거래 완료
                    }
                    TransferCategory.EXTERNAL -> {
                        // 타행 거래: 50% 확률로 출금(송금) 또는 입금(수신) 중 하나만 생성
                        val isOutgoing = Random.nextBoolean()
                        val externalBankCode = bankCodes.random()
                        val externalAccountNumber = generateRandomAccountNumber()

                        // 현재 잔액 가져오기 (또는 초기화)
                        val balanceBefore = getOrInitBalance(accountId)
                        val balanceAfter = if (isOutgoing) balanceBefore - amount else balanceBefore + amount

                        val row = generateTransactionRow(
                            groupId = groupId,
                            accountId = accountId,
                            entryType = if (isOutgoing) EntryType.DEBIT else EntryType.CREDIT,
                            amount = amount,
                            transferCategory = transferCategory,
                            status = status,
                            counterBankCode = externalBankCode,
                            counterAccountNumber = externalAccountNumber,
                            counterAccountId = null,
                            balanceBefore = balanceBefore,
                            balanceAfter = balanceAfter,
                            failureReason = failureReason,
                            memo = if (isOutgoing) "타행 송금" else "타행 수신",
                            transactionDate = now,
                            senderName = if (isOutgoing) accountHolderName else counterAccountHolderName,
                            receiverName = if (isOutgoing) counterAccountHolderName else accountHolderName
                        )
                        writer.write(row)
                        writer.newLine()
                        recordCount++

                        // 잔액 업데이트 (성공한 거래만)
                        if (status == TransactionStatus.SUCCESS) {
                            updateBalance(accountId, balanceAfter)
                        }

                        transactionCount++  // 1건의 거래 완료
                    }
                    TransferCategory.DEPOSIT -> {
                        // 입금만 생성 (1건의 거래 = 1개의 레코드)

                        // 현재 잔액 가져오기 (또는 초기화)
                        val balanceBefore = getOrInitBalance(accountId)
                        val balanceAfter = balanceBefore + amount

                        val row = generateTransactionRow(
                            groupId = groupId,
                            accountId = accountId,
                            entryType = EntryType.CREDIT,
                            amount = amount,
                            transferCategory = transferCategory,
                            status = status,
                            counterBankCode = "\\N",
                            counterAccountNumber = "\\N",
                            counterAccountId = null,
                            balanceBefore = balanceBefore,
                            balanceAfter = balanceAfter,
                            failureReason = failureReason,
                            memo = "현금 입금",
                            transactionDate = now,
                            senderName = "현금",
                            receiverName = accountHolderName
                        )
                        writer.write(row)
                        writer.newLine()
                        recordCount++

                        // 잔액 업데이트 (성공한 거래만)
                        if (status == TransactionStatus.SUCCESS) {
                            updateBalance(accountId, balanceAfter)
                        }

                        transactionCount++  // 1건의 거래 완료
                    }
                    TransferCategory.WITHDRAWAL -> {
                        // 출금만 생성 (1건의 거래 = 1개의 레코드)

                        // 현재 잔액 가져오기 (또는 초기화)
                        val balanceBefore = getOrInitBalance(accountId)
                        val balanceAfter = balanceBefore - amount

                        val row = generateTransactionRow(
                            groupId = groupId,
                            accountId = accountId,
                            entryType = EntryType.DEBIT,
                            amount = amount,
                            transferCategory = transferCategory,
                            status = status,
                            counterBankCode = "\\N",
                            counterAccountNumber = "\\N",
                            counterAccountId = null,
                            balanceBefore = balanceBefore,
                            balanceAfter = balanceAfter,
                            failureReason = failureReason,
                            memo = "현금 출금",
                            transactionDate = now,
                            senderName = accountHolderName,
                            receiverName = "ATM"
                        )
                        writer.write(row)
                        writer.newLine()
                        recordCount++

                        // 잔액 업데이트 (성공한 거래만)
                        if (status == TransactionStatus.SUCCESS) {
                            updateBalance(accountId, balanceAfter)
                        }

                        transactionCount++  // 1건의 거래 완료
                    }
                }

                // 진행 상황 로깅
                if (transactionCount % batchSize == 0L) {
                    println("$transactionCount / $totalTransactions 거래 생성됨 (레코드: $recordCount 개)...")
                }
            }

            println("TSV 파일 생성 완료!")
            println("총 $transactionCount 건의 거래, $recordCount 개의 레코드 생성 완료.")
        }
    }

    println("총 소요 시간: ${elapsedMillis / 1000.0} 초")
    println("파일 위치: $filePath")
}

// TransactionEntity의 스키마에 맞춰 TSV 한 줄을 생성한다.
// id 컬럼은 AUTO_INCREMENT이므로 제외한다.
private fun generateTransactionRow(
    groupId: String,
    accountId: Long,
    entryType: EntryType,
    amount: BigDecimal,
    transferCategory: TransferCategory,
    status: TransactionStatus,
    counterBankCode: String,
    counterAccountNumber: String,
    counterAccountId: Long?,
    balanceBefore: BigDecimal,
    balanceAfter: BigDecimal,
    failureReason: String,
    memo: String,
    transactionDate: LocalDateTime,
    senderName: String,
    receiverName: String
): String {
    // NULL 가능 컬럼 처리 (counterAccountId)
    val counterAccountIdStr = counterAccountId?.toString() ?: "\\N"

    // LOAD DATA 명령어에서 지정할 순서
    return listOf(
        groupId,                // transaction_group_id
        accountId,              // account_id
        entryType,              // entry_type
        amount,                 // amount
        transferCategory,       // transfer_category
        status,                 // status
        counterBankCode,        // counter_bank_code
        counterAccountNumber,   // counter_account_number
        counterAccountIdStr,    // counter_account_id
        balanceBefore,          // balance_before
        balanceAfter,           // balance_after
        failureReason,          // failure_reason
        memo,                   // memo
        senderName,             // sender_name
        receiverName,           // receiver_name
        transactionDate         // transaction_date (Java 1.8+의 기본 ISO 포맷은 MySQL이 인식함)
    ).joinToString(separator = "\t") // 탭(Tab)으로 구분
}