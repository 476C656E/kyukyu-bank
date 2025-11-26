package dev.kyukyubank.banking.common.utils.creator

import dev.kyukyubank.banking.common.enums.AccountType
import dev.kyukyubank.banking.common.enums.BankCode
import dev.kyukyubank.banking.common.enums.EntryType
import dev.kyukyubank.banking.common.enums.TransactionStatus
import dev.kyukyubank.banking.common.enums.TransferCategory
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.system.measureTimeMillis

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
    // 총 거래 건수
    const val TOTAL_TRANSACTIONS = 2_000_000L

    // 배치 크기
    const val BATCH_SIZE = 100_000L

    // 파일 경로
    const val TX_FILE_PATH: String = "./data/transaction_history.tsv"
    const val LEDGER_FILE_PATH: String = "./data/ledger.tsv"

    // 생성할 가상 계좌 수
    const val MAX_ACCOUNT_ID = 100_000L

    // 은행 코드
    val BANK_CODES = listOf(
        BankCode.KYUKYU_BANK,
        BankCode.Y_BANK,
        BankCode.X_BANK,
        BankCode.Z_BANK
    ).map { it.code }

    const val MIN_AMOUNT = 100.0
    const val MAX_AMOUNT = 1_000_000.0
    const val MIN_INITIAL_BALANCE = 100_000.0
    const val MAX_INITIAL_BALANCE = 10_000_000.0
    const val FAILURE_RATE = 0.03
    const val DATE_RANGE_DAYS = 365L
    
    // 분포 확률 (당행 60%, 타행 40%)
    const val INTERNAL_TRANSFER_RATE = 0.6 

    // 실패 사유
    val FAILURE_REASONS = listOf("잔액 부족", "일일 한도 초과", "계좌 정지", "유효하지 않은 계좌")
}

object UserConfig {
    const val TEST_ACCOUNT_ID = 1L
    const val TEST_ACCOUNT_NAME = "치와와"
    const val TEST_BANK_CODE = "1004"
    const val TEST_INITIAL_BALANCE = 100_000.0
    const val TEST_ACCOUNT_NUMBER = "100000000013"
    const val TEST_ACCOUNT_TRANSACTION_RATE = 0.05
}

// 계좌 잔액 캐시
private val accountBalances = mutableMapOf<Long, BigDecimal>()

private fun getOrInitBalance(accountId: Long): BigDecimal {
    return accountBalances.getOrPut(accountId) {
        if (accountId == UserConfig.TEST_ACCOUNT_ID) {
            BigDecimal(UserConfig.TEST_INITIAL_BALANCE).setScale(2, RoundingMode.HALF_UP)
        } else {
            BigDecimal(Random.nextDouble(DataConfig.MIN_INITIAL_BALANCE, DataConfig.MAX_INITIAL_BALANCE))
                .setScale(2, RoundingMode.HALF_UP)
        }
    }
}

private fun updateBalance(accountId: Long, newBalance: BigDecimal) {
    accountBalances[accountId] = newBalance
}

private fun generateRandomName() = "${NameData.LAST_NAMES.random()}" + "${NameData.FIRST_NAMES.random()}"

private fun getAccountHolderName(accountId: Long): String {
    return if (accountId == UserConfig.TEST_ACCOUNT_ID) UserConfig.TEST_ACCOUNT_NAME else generateRandomName()
}

private fun selectTransferCategory(): TransferCategory {
    return if (Random.nextDouble() < DataConfig.INTERNAL_TRANSFER_RATE) {
        TransferCategory.INTERNAL
    } else {
        TransferCategory.EXTERNAL
    }
}

private fun generateAccountNumber(accountId: Long): String {
    return if (accountId == UserConfig.TEST_ACCOUNT_ID) UserConfig.TEST_ACCOUNT_NUMBER else AccountType.DEPOSIT.accountTypeCode + accountId.toString().padStart(9, '0')
}

private fun generateRandomAccountNumber() = (100000000000L..999999999999L).random().toString()

private fun selectAccountId(maxAccountId: Long): Long {
    if (Random.nextDouble() < UserConfig.TEST_ACCOUNT_TRANSACTION_RATE) return UserConfig.TEST_ACCOUNT_ID
    // 간단한 파레토 로직
    val activeThreshold = (maxAccountId * 0.2).toLong()
    return if (Random.nextDouble() < 0.8) Random.nextLong(1, activeThreshold + 1)
    else Random.nextLong(activeThreshold + 1, maxAccountId + 1)
}

// 테스트 가능하도록 분리된 함수
fun generateTsvData(
    totalTransactions: Long,
    txFilePath: String,
    ledgerFilePath: String
) {
    val txFile = File(txFilePath)
    val ledgerFile = File(ledgerFilePath)
    txFile.parentFile.mkdirs()

    println("데이터 생성 시작... (목표: $totalTransactions 건)")
    
    // 캐시 초기화 (테스트 시 잔액 꼬임 방지)
    accountBalances.clear()

    val elapsedMillis = measureTimeMillis {
        txFile.bufferedWriter().use { txWriter ->
            ledgerFile.bufferedWriter().use { ledgerWriter ->
                
                var txIdCounter = 1L 
                var ledgerIdCounter = 1L 
                
                var createdTxCount = 0L

                while (createdTxCount < totalTransactions) {
                    val now = LocalDateTime.now().minusDays(Random.nextLong(0, DataConfig.DATE_RANGE_DAYS + 1))
                    val transferCategory = selectTransferCategory()
                    
                    val accountId = selectAccountId(DataConfig.MAX_ACCOUNT_ID)
                    var counterAccountId = selectAccountId(DataConfig.MAX_ACCOUNT_ID)
                    while (counterAccountId == accountId) counterAccountId = selectAccountId(DataConfig.MAX_ACCOUNT_ID)

                    val amount = BigDecimal(Random.nextDouble(DataConfig.MIN_AMOUNT, DataConfig.MAX_AMOUNT)).setScale(2, RoundingMode.HALF_UP)
                    val status = if (Random.nextDouble() < DataConfig.FAILURE_RATE) TransactionStatus.FAILED else TransactionStatus.SUCCESS
                    val failureReason = if (status == TransactionStatus.FAILED) DataConfig.FAILURE_REASONS.random() else "\\N"

                    val senderName = getAccountHolderName(accountId)
                    val receiverName = getAccountHolderName(counterAccountId)

                    // --- 1. Transaction Record 준비 ---
                    var txRow = ""
                    
                    var senderBalanceBefore = BigDecimal.ZERO
                    var senderBalanceAfter = BigDecimal.ZERO
                    var receiverBalanceBefore = BigDecimal.ZERO
                    var receiverBalanceAfter = BigDecimal.ZERO

                    when (transferCategory) {
                        TransferCategory.INTERNAL -> {
                            // Transaction 생성 (당행 이체)
                            txRow = generateTransactionRow(
                                id = txIdCounter,
                                transferCategory = transferCategory,
                                senderAccountId = accountId,
                                senderName = senderName,
                                receiverBankCode = UserConfig.TEST_BANK_CODE,
                                receiverAccountNumber = generateAccountNumber(counterAccountId),
                                receiverAccountId = counterAccountId,
                                receiverName = receiverName,
                                amount = amount,
                                status = status,
                                failureReason = failureReason,
                                transactionDate = now
                            )

                            // Ledger 생성 (Sender: 출금)
                            senderBalanceBefore = getOrInitBalance(accountId)
                            senderBalanceAfter = senderBalanceBefore - amount 
                            if (status == TransactionStatus.SUCCESS) {
                                val ledgerSender = generateLedgerRow(
                                    id = ledgerIdCounter++,
                                    transactionId = txIdCounter,
                                    accountId = accountId,
                                    entryType = EntryType.DEBIT,
                                    amount = amount,
                                    balanceAfter = senderBalanceAfter,
                                    memo = receiverName, 
                                    createdAt = now
                                )
                                ledgerWriter.write(ledgerSender)
                                ledgerWriter.newLine()
                                updateBalance(accountId, senderBalanceAfter)
                            }

                            // Ledger 생성 (Receiver: 입금)
                            receiverBalanceBefore = getOrInitBalance(counterAccountId)
                            receiverBalanceAfter = receiverBalanceBefore + amount
                            if (status == TransactionStatus.SUCCESS) {
                                val ledgerReceiver = generateLedgerRow(
                                    id = ledgerIdCounter++,
                                    transactionId = txIdCounter,
                                    accountId = counterAccountId,
                                    entryType = EntryType.CREDIT,
                                    amount = amount,
                                    balanceAfter = receiverBalanceAfter,
                                    memo = senderName, 
                                    createdAt = now
                                )
                                ledgerWriter.write(ledgerReceiver)
                                ledgerWriter.newLine()
                                updateBalance(counterAccountId, receiverBalanceAfter)
                            }
                        }
                        TransferCategory.EXTERNAL -> {
                            // 타행 이체 (송금 or 수취 50%)
                            val isOutgoing = Random.nextBoolean()
                            val externalBank = DataConfig.BANK_CODES.random()
                            val externalAccount = generateRandomAccountNumber()
                            
                            txRow = generateTransactionRow(
                                id = txIdCounter,
                                transferCategory = transferCategory,
                                senderAccountId = if (isOutgoing) accountId else null,
                                senderName = if (isOutgoing) senderName else "타행홍길동",
                                receiverBankCode = if (isOutgoing) externalBank else UserConfig.TEST_BANK_CODE,
                                receiverAccountNumber = if (isOutgoing) externalAccount else generateAccountNumber(accountId),
                                receiverAccountId = if (isOutgoing) null else accountId,
                                receiverName = if (isOutgoing) "타행김철수" else senderName,
                                amount = amount,
                                status = status,
                                failureReason = failureReason,
                                transactionDate = now
                            )

                            // 우리 은행 계좌에 대한 Ledger 1건만 발생
                            val myAccountId = accountId 
                            val myEntryType = if (isOutgoing) EntryType.DEBIT else EntryType.CREDIT
                            val myMemo = if (isOutgoing) "타행김철수" else "타행홍길동"
                            
                            val myBalanceBefore = getOrInitBalance(myAccountId)
                            val myBalanceAfter = if (isOutgoing) myBalanceBefore - amount else myBalanceBefore + amount
                            
                            if (status == TransactionStatus.SUCCESS) {
                                val ledgerRow = generateLedgerRow(
                                    id = ledgerIdCounter++,
                                    transactionId = txIdCounter,
                                    accountId = myAccountId,
                                    entryType = myEntryType,
                                    amount = amount,
                                    balanceAfter = myBalanceAfter,
                                    memo = myMemo,
                                    createdAt = now
                                )
                                ledgerWriter.write(ledgerRow)
                                ledgerWriter.newLine()
                                updateBalance(myAccountId, myBalanceAfter)
                            }
                        }
                    }

                    // Transaction은 실패해도 무조건 기록
                    txWriter.write(txRow)
                    txWriter.newLine()

                    txIdCounter++
                    createdTxCount++
                    
                    if (createdTxCount % DataConfig.BATCH_SIZE == 0L) {
                        println("$createdTxCount / $totalTransactions 처리 중...")
                    }
                }
            }
        }
    }
    println("완료! 시간: ${elapsedMillis/1000}초")
}

fun main() {
    generateTsvData(
        DataConfig.TOTAL_TRANSACTIONS,
        DataConfig.TX_FILE_PATH,
        DataConfig.LEDGER_FILE_PATH
    )
}

private fun generateTransactionRow(
    id: Long,
    transferCategory: TransferCategory,
    senderAccountId: Long?,
    senderName: String,
    receiverBankCode: String,
    receiverAccountNumber: String,
    receiverAccountId: Long?,
    receiverName: String,
    amount: BigDecimal,
    status: TransactionStatus,
    failureReason: String,
    transactionDate: LocalDateTime
): String {
    return listOf(
        id,
        transferCategory,
        senderAccountId?.toString() ?: "\\N",
        senderName,
        receiverBankCode,
        receiverAccountNumber,
        receiverAccountId?.toString() ?: "\\N",
        receiverName,
        amount,
        status,
        failureReason,
        transactionDate
    ).joinToString("\t")
}

private fun generateLedgerRow(
    id: Long,
    transactionId: Long,
    accountId: Long,
    entryType: EntryType,
    amount: BigDecimal,
    balanceAfter: BigDecimal,
    memo: String,
    createdAt: LocalDateTime
): String {
    return listOf(
        id,
        transactionId,
        accountId,
        entryType,
        amount,
        balanceAfter,
        memo,
        createdAt
    ).joinToString("\t")
}
