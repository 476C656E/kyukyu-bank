package dev.kyukyubank.banking.core.domain

import dev.kyukyubank.banking.storage.persistence.TransactionRepository
import org.springframework.stereotype.Service

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository
) {

}