package dev.kyukyubank.banking.core.service

import dev.kyukyubank.banking.common.enums.*
import dev.kyukyubank.banking.common.utils.creator.generateTsvData
import dev.kyukyubank.banking.storage.persistence.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import kotlin.random.Random
import kotlin.system.measureTimeMillis

@Service
class MockDataService(
    private val transactionRepository: TransactionRepository,
    private val ledgerRepository: LedgerRepository
) {

    // JPA 방식: 하나씩 저장 (느림)
    @Transactional
    fun generateByJpa(count: Long): Long {
        return measureTimeMillis {
            for (i in 1..count) {
                val amount = BigDecimal(Random.nextInt(1000, 100000))
                
                // Transaction 저장
                val tx = TransactionEntity(
                    transferCategory = TransferCategory.INTERNAL,
                    senderAccountId = 1L,
                    senderName = "Sender",
                    receiverBankCode = "1004",
                    receiverAccountNumber = "100000002",
                    receiverAccountId = 2L,
                    receiverName = "Receiver",
                    amount = amount,
                    status = TransactionStatus.SUCCESS
                )
                val savedTx = transactionRepository.save(tx)
                
                // Ledger 저장 (2건)
                ledgerRepository.save(LedgerEntity(
                    transaction = savedTx,
                    accountId = 1L,
                    entryType = EntryType.DEBIT,
                    amount = amount,
                    balanceAfter = BigDecimal.ZERO,
                    memo = "To Receiver"
                ))
                
                ledgerRepository.save(LedgerEntity(
                    transaction = savedTx,
                    accountId = 2L,
                    entryType = EntryType.CREDIT,
                    amount = amount,
                    balanceAfter = BigDecimal.ZERO,
                    memo = "From Sender"
                ))
            }
        }
    }

    // TSV 방식: 파일 생성 (빠름)
    fun generateByTsv(count: Long): Long {
        return measureTimeMillis {
            val tempDir = "./data/mock_test"
            generateTsvData(
                totalTransactions = count,
                txFilePath = "$tempDir/transactions.tsv",
                ledgerFilePath = "$tempDir/ledgers.tsv"
            )
        }
    }
}
