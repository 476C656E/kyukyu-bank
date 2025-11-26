package dev.kyukyubank.banking.common.utils

import dev.kyukyubank.banking.common.enums.EntryType
import dev.kyukyubank.banking.common.enums.TransactionStatus
import dev.kyukyubank.banking.common.enums.TransferCategory
import dev.kyukyubank.banking.common.utils.creator.generateTsvData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.math.BigDecimal
import java.nio.file.Path

class TsvGeneratorTest {

    @Test
    fun `TSV 데이터 생성 및 복식부기 정합성 검증`(@TempDir tempDir: Path) {
        // 1. Given: 테스트용 파일 경로 설정
        val txFile = tempDir.resolve("test_transactions.tsv").toFile()
        val ledgerFile = tempDir.resolve("test_ledgers.tsv").toFile()
        val totalCount = 1000L // 1000건 테스트

        // 2. When: 데이터 생성 실행
        generateTsvData(totalCount, txFile.absolutePath, ledgerFile.absolutePath)

        // 3. Then: 파일이 생성되었는지 확인
        assertTrue(txFile.exists(), "Transaction 파일이 생성되어야 합니다.")
        assertTrue(ledgerFile.exists(), "Ledger 파일이 생성되어야 합니다.")

        // 4. Then: 파일 내용 파싱
        val transactions = parseTransactions(txFile)
        val ledgers = parseLedgers(ledgerFile)

        println("생성된 Transaction: ${transactions.size}건")
        println("생성된 Ledger: ${ledgers.size}건")

        assertEquals(totalCount, transactions.size.toLong(), "요청한 개수만큼 Transaction이 생성되어야 합니다.")

        // 5. 정합성 검증 로직 수행
        var successCount = 0
        var failCount = 0
        
        transactions.forEach { tx ->
            val relatedLedgers = ledgers.filter { it.transactionId == tx.id }

            if (tx.status == TransactionStatus.FAILED) {
                // 실패한 거래는 원장 기록이 없어야 함 (현재 로직 기준)
                assertEquals(0, relatedLedgers.size, "실패한 거래(ID=${tx.id})는 원장 기록이 없어야 합니다.")
                failCount++
            } else {
                successCount++
                // 성공한 거래 검증
                when (tx.category) {
                    TransferCategory.INTERNAL -> {
                        // 당행 이체는 반드시 2개의 원장(출금, 입금)이 있어야 함
                        assertEquals(2, relatedLedgers.size, "당행 이체(ID=${tx.id})는 2개의 원장 기록이 필요합니다.")
                        
                        val debit = relatedLedgers.find { it.entryType == EntryType.DEBIT }
                        val credit = relatedLedgers.find { it.entryType == EntryType.CREDIT }
                        
                        assertTrue(debit != null, "INTERNAL 거래는 차변(DEBIT) 기록이 있어야 합니다.")
                        assertTrue(credit != null, "INTERNAL 거래는 대변(CREDIT) 기록이 있어야 합니다.")
                        
                        // 금액 일치 확인
                        assertEquals(0, tx.amount.compareTo(debit!!.amount), "Transaction 금액과 DEBIT 금액이 일치해야 합니다.")
                        assertEquals(0, tx.amount.compareTo(credit!!.amount), "Transaction 금액과 CREDIT 금액이 일치해야 합니다.")
                    }
                    TransferCategory.EXTERNAL -> {
                        // 타행 이체는 1개의 원장만 있어야 함 (내 계좌 변동분)
                        assertEquals(1, relatedLedgers.size, "타행 이체(ID=${tx.id})는 1개의 원장 기록이 필요합니다.")
                        
                        val ledger = relatedLedgers.first()
                        assertEquals(0, tx.amount.compareTo(ledger.amount), "Transaction 금액과 Ledger 금액이 일치해야 합니다.")
                    }
                }
            }
        }
        println("검증 완료: 성공 ${successCount}건, 실패 ${failCount}건")
    }

    // --- Helper Classes & Parsers ---

    data class TestTransaction(
        val id: Long,
        val category: TransferCategory,
        val amount: BigDecimal,
        val status: TransactionStatus
    )

    data class TestLedger(
        val id: Long,
        val transactionId: Long,
        val entryType: EntryType,
        val amount: BigDecimal
    )

    private fun parseTransactions(file: File): List<TestTransaction> {
        return file.readLines().map {
            val cols = it.split("\t")
            TestTransaction(
                id = cols[0].toLong(),
                category = TransferCategory.valueOf(cols[1]),
                amount = BigDecimal(cols[8]),
                status = TransactionStatus.valueOf(cols[9])
            )
        }
    }

    private fun parseLedgers(file: File): List<TestLedger> {
        return file.readLines().map {
            val cols = it.split("\t")
            TestLedger(
                id = cols[0].toLong(),
                transactionId = cols[1].toLong(),
                entryType = EntryType.valueOf(cols[3]),
                amount = BigDecimal(cols[4])
            )
        }
    }
}
