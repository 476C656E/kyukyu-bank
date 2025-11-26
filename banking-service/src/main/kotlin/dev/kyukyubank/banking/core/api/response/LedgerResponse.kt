package dev.kyukyubank.banking.core.api.response

import dev.kyukyubank.banking.common.enums.EntryType
import dev.kyukyubank.banking.storage.persistence.LedgerEntity
import java.math.BigDecimal
import java.time.LocalDateTime

data class LedgerResponse(
    val id: Long,
    val accountId: Long,
    val entryType: EntryType,
    val amount: BigDecimal,
    val balanceAfter: BigDecimal,
    val memo: String?,
    val transactionDate: LocalDateTime
) {
    companion object {
        fun from(entity: LedgerEntity): LedgerResponse {
            return LedgerResponse(
                id = entity.id,
                accountId = entity.accountId,
                entryType = entity.entryType,
                amount = entity.amount,
                balanceAfter = entity.balanceAfter,
                memo = entity.memo,
                transactionDate = entity.createdAt
            )
        }
    }
}
